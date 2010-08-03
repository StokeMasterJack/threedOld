package com.tms.threed.jpgGen.ezProcess;

import java.util.List;

public class Command {

    private List<String> command;

    public Command(List<String> command) {
        assert command != null;
        assert command.size() != 0;
        this.command = command;
    }

    public List<String> toList() {
        return command;
    }

    @Override public String toString() {
        return command.toString();
    }
}
