package com.tms.threed.featurePicker.client;

import com.google.gwt.user.client.ui.Composite;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class VarPanel extends Composite {

    @Nonnull protected final VarPanelModel context;
    @Nonnull protected final Picks picks;
    
    @Nonnull protected final Var var;
    @Nonnull protected final List<Var> childVars;


    protected VarPanel(@Nonnull VarPanelModel context, @Nonnull Var var) {
        assert context != null;
        assert var != null;
        this.context = context;
        this.picks = context.getPicks();
        this.var = var;
        this.childVars = var.getChildVars();
    }

    public void refresh() {
        if (childVars == null) return;

        for (int i = 0; i < childVars.size(); i++) {
            
            Var childVar = childVars.get(i);
            VarPanel childVarPanel = context.getVarPanel(childVar);
            
            if (childVarPanel != null) childVarPanel.refresh();
            
        }
    }


}
