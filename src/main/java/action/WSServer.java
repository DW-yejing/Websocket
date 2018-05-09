package action;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

//ws://127.0.0.1:8087/Demo1/ws/����
@ServerEndpoint("/ws/{user}")
public class WSServer {
	private String currentUser;
	//��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ�ġ�
    private static int onlineCount = 0;

    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��MyWebSocket������Ҫʵ�ַ�����뵥һ�ͻ���ͨ�ŵĻ�������ʹ��Map����ţ�����Key����Ϊ�û���ʶ
    private static CopyOnWriteArraySet<WSServer> webSocketSet = new CopyOnWriteArraySet<WSServer>();
    private Session session;
	//���Ӵ�ʱִ��
	@OnOpen
	public void onOpen(@PathParam("user") String user, Session session) {
		currentUser = user;
		this.session = session;
	    webSocketSet.add(this);     //����set��
		System.out.println("Connected ... " + session.getId());
	}

	//�յ���Ϣʱִ��
	@OnMessage
	public void onMessage(String message, Session session) {
		//Ⱥ����Ϣ
        for(WSServer item: webSocketSet){
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
		
	}
	
	public void sendMessage(String message) throws IOException{
	    this.session.getBasicRemote().sendText(message); 
	}
	//���ӹر�ʱִ��
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		webSocketSet.remove(this);  //��set��ɾ��
		System.out.println(String.format("Session %s closed because of %s", session.getId(), closeReason));
	}

	//���Ӵ���ʱִ��
	@OnError
	public void onError(Throwable t) {
		t.printStackTrace();
	}
}
