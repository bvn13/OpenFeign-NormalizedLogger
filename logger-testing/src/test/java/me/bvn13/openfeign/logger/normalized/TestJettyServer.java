package me.bvn13.openfeign.logger.normalized;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class TestJettyServer {

    public static final int PORT = 8090;
    private Server server;

    public TestJettyServer() {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] {connector});
    }

    public void start() {
        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
        servletHandler.addServletWithMapping(TestBlockingServlet.class, "/status");
        new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        TestJettyServer jettyServer;
        jettyServer = new TestJettyServer();
        try {
            jettyServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
