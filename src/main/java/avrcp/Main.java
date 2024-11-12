package avrcp;

import java.util.HashMap;
import java.util.Map;

import org.bluez.Media1;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

//TODO - device address should be an argument
//TODO - add logging support

public class Main {
	public static final void main(String[] args) throws Exception {
		try(DBusConnection connection = DBusConnectionBuilder.forSystemBus().build()){
			Media1 service = connection.getRemoteObject("org.bluez", "/org/bluez/hci0", Media1.class);
			ConnectedPropertyChangeHandler handler = new ConnectedPropertyChangeHandler(service);
			
			Properties properties = connection.getRemoteObject("org.bluez", "/org/bluez/hci0/dev_C8_7B_23_96_55_30", Properties.class);
			boolean value = ((Boolean) properties.Get("org.bluez.Device1", "Connected")).booleanValue();

			if(value){
				handler.createAvrcpPlayer();
			}

			connection.addSigHandler(
				Properties.PropertiesChanged.class, 
				properties,
				handler
			);

			Thread.currentThread().join();
		}
	}
}
