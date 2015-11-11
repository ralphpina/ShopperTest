package net.ralphpina.shoppertest.events;

public class SelectionEvent {

    private final int mPosition;

    public SelectionEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
