package io.gitlab.jfronny.javagi.example;

import org.gnome.gio.ApplicationFlags;
import org.gnome.gtk.*;

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

        var button = Button.withLabel("Hello, World!");
        button.onClicked(window::close);

        box.append(button);
        window.setChild(box);
        window.show();
    }

    private int run(String[] args) {
        return app.run(args);
    }

    public static void main(String[] args) {
        System.exit(new Main().run(args));
    }
}