package com.navyn.emissionlog.modules.workspace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navyn.emissionlog.modules.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="recordEntities")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JsonIgnore
    private User admin = null;

    @OneToMany
    @JsonIgnore
    private List<User> dataEntry;
}
