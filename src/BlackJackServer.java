// Todo list
// 1. J,Q,K
// 2. improve dealer strategy
// 3. display history
// 4. split, insurance

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

class BlackJackServer {
    public static int inPort = 9999;
    public static Vector<Client> clients = new Vector<Client>();


    public static void main(String[] args) throws Exception {
        new BlackJackServer().createServer();
    }

    public void createServer() throws Exception {
        System.out.println("Server start running ..");
        ServerSocket server = new ServerSocket(inPort);
        while (true) {
            Socket socket = server.accept();
            Client c = new Client(socket);
            clients.add(c);
        }
    }

    public int getCard() {
        Random random = new Random();
        int newcard = random.nextInt(13)+1;
        System.out.println("new card is "+newcard);
        return newcard;
    }


    class Client extends Thread {
        Socket socket;
        PrintWriter out = null;
        BufferedReader in = null;
        int card;

        public Client(Socket socket) throws Exception {
            System.out.println("\n\n"+socket.getInetAddress()+ "  join ");
            this.socket = socket;
            card = 0;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            start();

            int newCard = getCard();
            card += newCard;
            send(""+newCard);
            System.out.println(socket.getInetAddress()+ "  has "+ card);

        }

        public void send(String msg) {
            out.println(msg);
        }


        @Override
        public void run() {
            String msg;
            int newcard;

            try {
                while(true) {
                    msg = in.readLine();

                    if (msg.equalsIgnoreCase("1")) {
                        System.out.println(socket.getInetAddress()+" hit");
                        newcard = getCard();
                        card += newcard;
                        System.out.println(socket.getInetAddress()+" has "+card);
                        send(""+newcard);

                        if (card > 21) {
                            System.out.println(socket.getInetAddress()+" has "+card+", Over 21! \n\n");
                            card = getCard();
                            send(""+card);
                        }
                        if (card == 21) {
                            System.out.println(socket.getInetAddress()+" has "+card+", BLACKJACK! \n\n");
                            card = getCard();
                            send(""+card);
                        }

                    }
                    else if (msg.equalsIgnoreCase("2")) {
                        System.out.println(socket.getInetAddress()+" hold");
                        Random random = new Random();
                        int dealer = random.nextInt(11) + 16;
                        System.out.println("Dealer's hand: " + dealer);
                        send(""+dealer);

                        if ((card > dealer) || (dealer > 21)) {
                            System.out.println(socket.getInetAddress()+ " win!\n\n");
                        }
                        else if (card < dealer) {
                            System.out.println("Dealer win!\n\n");
                        }
                        else {
                            System.out.println("tie! \n\n");
                        }

                        card = getCard();
                        send(""+card);

                    }
                    else {
                        System.out.println(socket.getInetAddress()+" Exit");
                        in.close();
                        out.close();
                        socket.close();
                        break;
                    }

                }
            }
            catch (IOException e) { }
        }

    }

}