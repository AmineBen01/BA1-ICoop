package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.engine.actor.Dialog;


public interface DialogHandler {

    /**
     * @param dialog (Dialog): dialog to publish.
     */
    void publish(Dialog dialog);
}
