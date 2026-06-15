package com.rao.multiaccountcredentials.comman;

public class VerificationResult {
    private boolean verified;
    private double distance;
    private double threshold;

    // Getter and Setter for verified
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    // Getter and Setter for distance
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    // Getter and Setter for threshold
    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
