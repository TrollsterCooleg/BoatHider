package me.cooleg.boathider;

public class IncompatibleVersionException extends RuntimeException{
    public IncompatibleVersionException(String version) {
        super("Version " + version + " incompatible with BoatHider plugin!");
    }
}
