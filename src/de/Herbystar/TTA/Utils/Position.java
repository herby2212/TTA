package de.Herbystar.TTA.Utils;

import org.bukkit.entity.Player;
import org.bukkit.Location;

public class Position {
	
    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return null;
        }
    }
    
    public static Location getPlayerLocEnderDragon(Player p) {
    Location loc = p.getLocation();
    switch (getCardinalDirection(p)) {
      case ("N") :
          loc.add(0, 0, -150);
          break;
      case ("E") :
          loc.add(150, 0, 0);
          break;
      case ("S") :
          loc.add(0, 0, 150);
          break;
      case ("W") :
          loc.add(-150, 0, 0);
          break;
      case ("NE") :
          loc.add(150, 0, -150);
          break;
      case ("SE") :
          loc.add(150, 0, 150);
          break;
      case ("NW") :
          loc.add(-150, 0, -150);
          break;
    case ("SW") :
          loc.add(-150, 0, 150);
          break;
    }
    
    
    return loc;    
    }
    
    public static Location getPlayerLocWither(Player p) {
        Location loc = p.getLocation();
        switch (getCardinalDirection(p)) {
          case ("N") :
              loc.add(0, 0, -50);
              break;
          case ("E") :
              loc.add(50, 0, 0);
              break;
          case ("S") :
              loc.add(0, 0, 50);
              break;
          case ("W") :
              loc.add(-50, 0, 0);
              break;
          case ("NE") :
              loc.add(50, 0, -50);
              break;
          case ("SE") :
              loc.add(50, 0, 50);
              break;
          case ("NW") :
              loc.add(-50, 0, -50);
              break;
        case ("SW") :
              loc.add(-50, 0, 50);
              break;
        }
        
        return loc;
        
        }
}
