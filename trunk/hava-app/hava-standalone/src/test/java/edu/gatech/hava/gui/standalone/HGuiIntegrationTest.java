package edu.gatech.hava.gui.standalone;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HGuiIntegrationTest {

    private FrameFixture window;

    @Before public void setUp() {

        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {

            @Override
            protected JFrame executeInEDT() throws Throwable {

                HGUI hgui = new HGUI();

                return hgui.getFrame();

            }

        });

        window = new FrameFixture(frame);

        window.show();

    }

    @Test public void run() {

    }

    @After public void tearDown() {

        window.cleanUp();

    }

}
