package io.gitlab.jfronny.javagi.example;

import org.gtk.gio.ApplicationFlags;
import org.gtk.gtk.*;

public class Main {
    private final Application app;

    public Main() {
        this.app = new Application("org.gtk.example", ApplicationFlags.FLAGS_NONE);
        app.onActivate(this::onActivate);
    }

    private void onActivate() {
        var window = new ApplicationWindow(app);
        window.setTitle("Hello");
        window.setDefaultSize(300, 200);

        var box = new Box(Orientation.VERTICAL, 0);
        box.setHalign(Align.CENTER);
        box.setValign(Align.CENTER);

        var button = Button.newWithLabel("Hello, World!");
        button.onClicked(window::close);

        box.append(button);
        window.setChild(box);
        window.show();
    }

    private void run(String[] args) {
        app.run(args.length, args);
    }

    public static void main(String[] args) {
        new Main().run(args);
    }
}