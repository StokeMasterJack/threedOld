package com.tms.threed.previewPanel.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.tms.threed.util.lang.shared.Path;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class BlinkOverlay extends Image {

    public BlinkOverlay() {
        setVisible(false);
        ensureDebugId(getSimpleName(this));
    }

    public void doFeatureBlink(Path pngToBlink, Command postCommand) {
        assert pngToBlink != null;
        String url = pngToBlink.toString();
        assert url != null;

        setVisible(false);
        setUrl(url);
        toggleVisibility(0, postCommand);
    }

    private void toggleVisibility(final int count, final Command postCommand) {
        setVisible(!isVisible());

        if (count > 10) {
            setVisible(false);
            DeferredCommand.addCommand(postCommand);
            return;
        }
        Timer t = new Timer() {
            @Override public void run() {
                toggleVisibility(count + 1, postCommand);
            }
        };
        t.schedule(100);
    }
}
