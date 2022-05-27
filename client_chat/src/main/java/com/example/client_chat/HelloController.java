package com.example.client_chat;

import com.example.client_chat.history.HistoryService;
import com.example.server.RegServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class HelloController {

    private boolean isAuthorized;

    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    Button button;

    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button enter;

    @FXML
    HBox upperPanel;
    @FXML
    HBox bottomPanel;

    @FXML
    ListView clientList;

    @FXML
    Button createAccount;
    @FXML
    TextField loginCreate;
    @FXML
    PasswordField passwordCreate;
    @FXML
    TextField nickCreate;


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    String IP_ADDRESS = "localhost";
    int PORT = 8189;
    String nickname = "";

    @FXML
    public void keyListenerCreateAccount(KeyEvent keyEvent) {
        if (keyEvent.getCode().getCode() == 10) {
            registration();
        }
    }

    @FXML
    public void registration() {

        RegServer.connect();
        boolean ul = RegServer.uniqueLogin(loginCreate.getText());
        boolean un = RegServer.uniqueNick(nickCreate.getText());
        String login = loginCreate.getText();
        String password = passwordCreate.getText();
        String nick = nickCreate.getText();

        if (!login.isEmpty()) {
            if (!password.isEmpty()) {
                if (!nick.isEmpty()) {
                    if (!ul) {
                        if (!un) {
                            RegServer.registration(loginCreate.getText(), passwordCreate.getText(), nickCreate.getText());
                            textArea.appendText("Вы зарегистрированы под ником " + nickCreate.getText() +
                                    "\n Теперь вы можете войти в свой аккаунт ^ \n");
                        } else {
                            textArea.appendText("Пользователь с таким ником уже зарегистрирован :( \n");
                        }
                    } else {
                        textArea.appendText("Пользователь с таким логином уже зарегистрирован :( \n");
                    }
                } else {
                    textArea.appendText("Введите ник \n");
                }
            } else {
                textArea.appendText("Введите пароль! \n");
            }
        } else {
            textArea.appendText("Введите логин! \n");
        }

        RegServer.disconnect();
        loginCreate.clear();
        passwordCreate.clear();
        nickCreate.clear();
    }

    @FXML
    public void keyListener(KeyEvent keyEvent) {
        if (keyEvent.getCode().getCode() == 10) {
            sendMessage();
        }
    }

    public void setActive(Boolean isAuthorized) {
        this.isAuthorized = isAuthorized;

        if (!isAuthorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
            createAccount.setVisible(false);
            createAccount.setManaged(false);
            loginCreate.setVisible(false);
            loginCreate.setManaged(false);
            passwordCreate.setVisible(false);
            passwordCreate.setManaged(false);
            nickCreate.setVisible(false);
            nickCreate.setManaged(false);
        }
    }

    @FXML
    public void sendMessage() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void connect() {

        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            try {
                                String str = in.readUTF();
                                if (str.startsWith("/authOK")) {
                                    setActive(true);
                                    textArea.clear();
                                    textArea.appendText(str + "\n");
                                    nickname = str.substring(35).trim();
                                    HistoryService.saveHistory(nickname, "");
                                    textArea.appendText(HistoryService.loadHistory(nickname));
                                    break;
                                } else {
                                    textArea.appendText(str + "\n");
                                    HistoryService.saveHistory(nickname, str);
                                    File file = new File("src/main/java/com/example/client_chat/history/history_.txt");
                                    if(file.exists()) file.delete();
                                }
                            } catch (SocketException e) {
                                System.out.println("Server don't callback");
                                break;
                            }
                        }


                        while (true) {
                            try {
                                String str = in.readUTF();

                                if (str.startsWith("/")) {
                                    if (str.startsWith("/show")) {
                                        String[] nicknames = str.split(" ");

                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                clientList.getItems().clear();
                                                for (int i = 1; i < nicknames.length; i++) {
                                                    clientList.getItems().add(nicknames[i]);
                                                }
                                            }
                                        });
                                    }
                                    if (str.equals("/end")) {
                                        HistoryService.saveHistory(nickname, str);
                                        break;
                                    }
                                } else {
                                    textArea.appendText(str + "\n");
                                    HistoryService.saveHistory(nickname, str);
                                }

                            } catch (SocketException e) {
                                System.out.println("Server don't callback");
                                HistoryService.saveHistory(nickname, "");
                                break;
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                            in.close();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void auth() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            if (loginField.getText().isBlank() || passwordField.getText().isBlank()) {
                textArea.appendText("Input login/Password\n");
                return;
            }

            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



