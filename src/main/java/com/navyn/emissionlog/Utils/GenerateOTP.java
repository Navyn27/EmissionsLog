package com.navyn.emissionlog.Utils;

import java.util.Random;

public class GenerateOTP {
    public static double generateOTP(){
        Random random = new Random();
        double otp = 100000 + random.nextInt(900000);
        return otp;
    }
}
