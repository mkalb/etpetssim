package de.mkalb.etpetssim.simulations.lab;

public final class LabControlViewModel {

    private Runnable onDrawButtonListener = () -> {};
    private Runnable onDrawModelButtonListener = () -> {};
    private Runnable onDrawTestButtonListener = () -> {};

    public LabControlViewModel() {
    }

    public void setOnDrawButtonListener(Runnable listener) {
        onDrawButtonListener = listener;
    }

    public void setOnDrawModelButtonListener(Runnable listener) {
        onDrawModelButtonListener = listener;
    }

    public void setOnDrawTestButtonListener(Runnable listener) {
        onDrawTestButtonListener = listener;
    }

    public void onDrawButtonClicked() {
        onDrawButtonListener.run();
    }

    public void onDrawModelButtonClicked() {
        onDrawModelButtonListener.run();
    }

    public void onDrawTestButtonClicked() {
        onDrawTestButtonListener.run();
    }

}
