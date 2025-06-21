public class Task {
    private String name;
    private int duration; // in hours
    private int remaining; // in minutes

    public Task(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.remaining = duration * 60;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getRemaining() {
        return remaining;
    }

    public void workOn(int minutes) {
        remaining -= minutes;
        if (remaining < 0) remaining = 0;
    }

    public boolean isCompleted() {
        return remaining <= 0;
    }
}