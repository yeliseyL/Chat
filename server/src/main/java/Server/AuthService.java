package Server;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password);
    boolean registration(String login, String password, String nickname);
    void changeNickname(String login, String newNickname);
}
