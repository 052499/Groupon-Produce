package com.example.farmerchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {
    private WebSocketClient webSocketClient;
    private EditText messageInput;
    private TextView chatMessages;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageInput = findViewById(R.id.messageInput);
        chatMessages = findViewById(R.id.chatMessages);
        sendButton = findViewById(R.id.sendButton);

        connectWebSocket();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                if (!message.isEmpty()) {
                    webSocketClient.send(message);
                    messageInput.setText("");
                }
            }
        });
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://your-server-ip:8080/chat"); // Change to your server IP
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                runOnUiThread(() -> chatMessages.append("Connected to Chat\n"));
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> chatMessages.append("Farmer: " + message + "\n"));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                runOnUiThread(() -> chatMessages.append("Chat Disconnected\n"));
            }

            @Override
            public void onError(Exception ex) {
                runOnUiThread(() -> chatMessages.append("Error: " + ex.getMessage() + "\n"));
            }
        };
        webSocketClient.connect();
    }
}

