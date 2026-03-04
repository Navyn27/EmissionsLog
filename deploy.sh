#!/bin/bash
set -e

echo "🚀 Starting intelligent deployment..."

# Navigate to repository directory
cd ~/Emissions-log-backend

# Pull latest code
echo "📥 Pulling latest code from Git..."
git pull origin main

# Get current commit hash
COMMIT_HASH=$(git rev-parse --short HEAD)
IMAGE_NAME="emissions-log:$COMMIT_HASH"

echo "Current commit: $COMMIT_HASH"

# Stop and remove old containers to prevent migration conflicts
echo "🧹 Stopping old containers..."
docker-compose down --remove-orphans || true

# Force remove old app container if it exists
docker rm -f emissions-log 2>/dev/null || true

# Remove old Docker Hub image if it exists
docker rmi sugirayvan/emissions-log:sleepysloth 2>/dev/null || true

# Check if rebuild is needed
if docker image inspect $IMAGE_NAME >/dev/null 2>&1; then
  echo "✓ Image $IMAGE_NAME already exists, skipping build"
else
  echo "🔨 Building new image: $IMAGE_NAME"
  docker build -t $IMAGE_NAME .
  
  # Tag as latest
  docker tag $IMAGE_NAME emissions-log:latest
  echo "✓ Image built successfully"
fi

# Update docker-compose to use this image
export IMAGE_TAG=$COMMIT_HASH

# Deploy with the specific image tag
echo "🚢 Deploying containers..."
docker-compose up -d --remove-orphans

# Cleanup old images (keep latest 3 to be safe)
echo "🧹 Cleaning up old images..."
IMAGES_TO_DELETE=$(docker images emissions-log --format "{{.ID}}" | tail -n +4)
if [ ! -z "$IMAGES_TO_DELETE" ]; then
  echo "$IMAGES_TO_DELETE" | xargs docker rmi || true
  echo "✓ Removed old images"
else
  echo "✓ No old images to clean"
fi

# Clean up dangling images
docker image prune -f >/dev/null 2>&1

echo ""
echo "✅ Deployment complete!"
echo "🏷️  Running image: $IMAGE_NAME"
echo ""
docker-compose ps
