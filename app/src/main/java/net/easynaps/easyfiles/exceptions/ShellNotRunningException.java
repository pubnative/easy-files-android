package net.easynaps.easyfiles.exceptions;

public class ShellNotRunningException extends Exception {
    public ShellNotRunningException() {
        super("Shell stopped running!");
    }
}
