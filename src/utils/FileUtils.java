/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import data.Gamedata;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils  {

    public static final String SERIALIZATION_FILE_NAME = "game.ser";

    public static Gamedata Load() {

            Gamedata gd = null;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileUtils.SERIALIZATION_FILE_NAME))) {
                gd = (Gamedata) ois.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        return gd;
    }

    public static void Save(Gamedata data) {
        try (ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(FileUtils.SERIALIZATION_FILE_NAME))) {
            oos.writeObject(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
