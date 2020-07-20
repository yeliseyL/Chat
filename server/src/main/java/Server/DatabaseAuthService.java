package Server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAuthService implements AuthService {

    DataBase dataBase;

    public DatabaseAuthService() {
        dataBase = new DataBase();
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return dataBase.getNickname(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        if (dataBase.getNickname(login, password) != null) {
            return false;
        }

        dataBase.addUser(nickname, login, password);
        return true;
    }

    public void changeNickname(String login, String newNickname) {
        dataBase.changeNickname(login, newNickname);
    }
}


