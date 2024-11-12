package avrcp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bluez.Media1;
import org.bluez.exceptions.BluezInvalidArgumentsException;
import org.bluez.exceptions.BluezNotSupportedException;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

public class ConnectedPropertyChangeHandler extends AbstractPropertiesChangedHandler {
	private Media1 service;
	private long timestamp;

	public ConnectedPropertyChangeHandler(Media1 service){
		this.service = service;
	}

	public void createAvrcpPlayer() throws BluezInvalidArgumentsException, BluezNotSupportedException {
		Map<String, Variant<?>> properties = new HashMap<>();
		properties.put("PlaybackStatus", new Variant<String>("Playing"));

		service.RegisterPlayer(new DBusPath("/org/bluez/hci0/avrcp_player"),  properties);

		timestamp = System.currentTimeMillis();
	}

	@Override
	public void handle(Properties.PropertiesChanged event){
		try{
			for(Map.Entry<String, Variant<?>> entry : event.getPropertiesChanged().entrySet()){
				if("Connected".equals(entry.getKey())){
					boolean value = (Boolean) entry.getValue().getValue();

					if(value){
						if(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timestamp) > 10){
							createAvrcpPlayer();	
						}
					}
				}
			}
		}
		catch(BluezInvalidArgumentsException | BluezNotSupportedException ex){
			ex.printStackTrace();
		}
	}
}
