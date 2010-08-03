package com.tms.threed.jpgGen.ezProcess;

import com.tms.threed.jpgGen.ezProcess.CommandBuilder;

import java.util.List;

public abstract class AbstractCommandBuilder implements CommandBuilder {


    public String commandToString() {
        List<String> list = this.buildCommand().toList();
        return commandToString(list);
    }


    public static String commandToString(List<String> command) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < command.size(); i++) {
            sb.append(command.get(i));
            if (i != command.size() - 1) sb.append(" ");
        }
        return sb.toString();
    }


}
