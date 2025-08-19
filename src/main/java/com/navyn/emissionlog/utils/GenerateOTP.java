package com.navyn.emissionlog.utils;

import java.util.Random;

public class GenerateOTP {
    public static double generateOTP(){
        Random random = new Random();
        double otp = 100000 + random.nextInt(900000);
        return otp;
    }
}
