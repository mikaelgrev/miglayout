module net.miginfocom.javafx {
    exports org.tbee.javafx.scene.layout;
    
    requires net.miginfocom.core;
	requires java.desktop;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
}