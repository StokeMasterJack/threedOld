package com.tms.configurator.admintool.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

public class AdminTool implements EntryPoint {

    Tree tree = new Tree();

    public void onModuleLoad() {

        RootLayoutPanel.get().add(tree);

    }
}
