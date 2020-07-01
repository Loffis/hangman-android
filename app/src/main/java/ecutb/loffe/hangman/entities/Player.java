package ecutb.loffe.hangman.entities;

import java.util.Objects;

public class Player {

    private int id;
    private String username;
    private String password;

    public Player(int id, String username, String password) {
        this.id = id;
        this.username = username.toUpperCase();
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id &&
                Objects.equals(username, player.username) &&
                Objects.equals(password, player.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }
}
