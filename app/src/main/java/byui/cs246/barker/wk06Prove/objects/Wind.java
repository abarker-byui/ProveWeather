package byui.cs246.barker.wk06Prove.objects;

public class Wind {
    private float _speed;

    public float getSpeed() { return _speed; }

    public String toString() {
        return String.format("Wind: %.1f mph", _speed);
    }
}
