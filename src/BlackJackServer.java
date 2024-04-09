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
        System.out.println("Wating clients to join...");
        while (true) {
            Socket socket = server.accept();    // main이 돌면 여기서 클라이언트의 접속을 기다린다.
            Client c = new Client(socket);
            clients.add(c);
        }
    }

    public int getCard() {
        Random random = new Random();
        int newcard = random.nextInt(13)+1;

        if (newcard == 1) {
            System.out.println("new card is Ace");
        }
        else if (newcard == 11) {
            System.out.println("new card is Jack"); // 해당 Client 가 getCard() method 로 카드를 뽑으면 Server Console 에 출력한다.
        }
        else if (newcard == 12) {
            System.out.println("new card is Queen"); // 해당 Client 가 getCard() method 로 카드를 뽑으면 Server Console 에 출력한다.
        }
        else if (newcard == 13) {
            System.out.println("new card is King"); // 해당 Client 가 getCard() method 로 카드를 뽑으면 Server Console 에 출력한다.
        }
        else {
            System.out.println("new card is "+newcard); // 해당 Client 가 getCard() method 로 카드를 뽑으면 Server Console 에 출력한다.
        }

        return newcard;
    }


    class Client extends Thread {
        Socket socket;
        PrintWriter out = null;
        BufferedReader in = null;
        int card;

        public Client(Socket socket) throws Exception {
            System.out.println("\n\n"+socket.getInetAddress()+ "  join ");  // Client 가 접속하면 해당 c의 주소와 Join을 server console에 출력한다.
            this.socket = socket;
            card = 0;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            start();    // Client run() 메서드를 병렬적으로 실행한다.

            int newCard = getCard();
            card += newCard;
            send(""+newCard);   // getCard() 로 뽑은 카드를 해당 C에게 뽑은 카드를 String 타입으로 보내준다.
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
                    msg = in.readLine();    // Client 가 접속하면 해당 c 객체는 여기서 입력을 기다린다.

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