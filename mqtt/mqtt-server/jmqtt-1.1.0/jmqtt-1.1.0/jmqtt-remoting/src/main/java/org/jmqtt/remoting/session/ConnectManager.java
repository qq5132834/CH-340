package org.jmqtt.remoting.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 
 * @author:  大聊
 * @Package:  org.jmqtt.remoting.session
 * @ClassName:  ConnectManager
 * @Description:  客户端会话session管理类
 * @date:  2019年6月2日 上午12:24:32
 * @email: 513283439@qq.com
 */
public class ConnectManager {

    private  Map<String, ClientSession> clientCache = new ConcurrentHashMap<>();

    private static final  ConnectManager INSTANCE  =  new ConnectManager();

    private ConnectManager(){};

    public static  ConnectManager getInstance(){
        return INSTANCE;
    }

    /***
     * 获取客户端信息
     * @param clientId
     * @return
     */
    public ClientSession getClient(String clientId){
        return this.clientCache.get(clientId);
    }

    /***
     * 添加客户端信息
     * @param clientId
     * @param clientSession
     * @return
     */
    public ClientSession putClient(String clientId,ClientSession clientSession){
        return this.clientCache.put(clientId,clientSession);
    }

    /***
     * 判断客户端是否存在
     * @param clientId
     * @return
     */
    public boolean containClient(String clientId){
        return this.clientCache.containsKey(clientId);
    }

    /***
     * 移除一个客户端信息
     * @param clientId
     * @return
     */
    public ClientSession removeClient(String clientId){
        return this.clientCache.remove(clientId);
    }
    
}
