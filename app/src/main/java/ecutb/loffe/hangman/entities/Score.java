package ecutb.loffe.hangman.entities;

public class Score implements Comparable<Score> {
    int id;
    String username;
    int score;

    public Score(int id, String username, int score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Score o) {
        return getScore().compareTo(o.getScore());
    }

    @Override
    public String toString() {
        return score + "" + username;
    }
}
