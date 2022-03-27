package lab1;
/*
    Można dodać tę klasę abstrakcyjną aby skrócić i uprościć klasy Grass i Animal o powtarzający się kod
 */
abstract class AbstractWorldMapElement implements IMapElement{
    protected Vector2d position;

    public Vector2d getPosition(){
        return this.position;
    }

}
