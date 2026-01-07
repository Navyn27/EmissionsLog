package com.navyn.emissionlog.modules.userManual;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserManualService {
    byte[] downloadUserManual() throws IOException;
    void uploadUserManual(MultipartFile file) throws IOException;
    boolean userManualExists();
}

