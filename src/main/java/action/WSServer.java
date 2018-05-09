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

//ws://127.0.0.1:8087/Demo1/ws/张三
@ServerEndpoint("/ws/{user}")
public class WSServer {
	private String currentUser;
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WSServer> webSocketSet = new CopyOnWriteArraySet<WSServer>();
    private Session session;
	//连接打开时执行
	@OnOpen
	public void onOpen(@PathParam("user") String user, Session session) {
		currentUser = user;
		this.session = session;
	    webSocketSet.add(this);     //加入set中
		System.out.println("Connected ... " + session.getId());
	}

	//收到消息时执行
	@OnMessage
	public void onMessage(String message, Session session) {
		//群发消息
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
	//连接关闭时执行
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		webSocketSet.remove(this);  //从set中删除
		System.out.println(String.format("Session %s closed because of %s", session.getId(), closeReason));
	}

	//连接错误时执行
	@OnError
	public void onError(Throwable t) {
		t.printStackTrace();
	}
}
