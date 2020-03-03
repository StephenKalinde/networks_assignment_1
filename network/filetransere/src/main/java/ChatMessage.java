/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Takaedza Chigwedere and Stephen Kalinde
 */
import java.io.*;
/*
03
 * This class defines the different type of messages that will be exchanged between the
Clients and the Server.
When talking from a Java Client to a Java Server a lot easier to pass Java objects, no
 need to count bytes or to wait for a line feed at the end of the frame
 */

public class ChatMessage implements Serializable {

     protected static final long serialVersionUID = 1112122200L;
    /** The different types of message sent by the Client

     WHOISIN to receive the list of the users connected

     MESSAGE an ordinary message

     LOGOUT to disconnect from the Server */

    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2 , FILE = 3, ALLFILES=4;

    private int type;

    private String message;
    private File file;

     

    // constructor

    ChatMessage(int type, String message) {

        this.type = type;

        this.message = message;
    }

    ChatMessage(int type, File file){

        this.type = type;

        this.file = file;
    }
    ChatMessage(int type)
    {
        this.type= type;
    }
    // getters

    int getType() {

        return type;
    }

    String getMessage() {

        return message;

    }

    File getFile()
    {
        return file;
    }

}

