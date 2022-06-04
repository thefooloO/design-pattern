package behavioral;

/**
 * 状态模式
 */

/**
 * 有限状态机(Finite State Machine, FSM)
 * 状态机有三个组成部分：状态(State)、事件(Event)、动作(Action)
 * 事件也称为转移条件, 事件会触发状态的转移及动作的执行
 */

import static behavioral.State.*;

/**
 * 例：超级马里奥(形态的转变)
 */
enum State {
    SMALL(0),
    SUPER(1),
    FIRE (2),
    CAPE (3);

    private int value;

    private State(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}


/**
 * 实现方式一：分支逻辑法      较难维护
 */
class MarioStateMachine1 {

    private int score;
    private State currentState;

    public int getScore() {
        return score;
    }

    public State getCurrentState() {
        return currentState;
    }

    public MarioStateMachine1() {
        this.score = 0;
        this.currentState = State.SMALL;
    }

    public void obtainMushRoom() {
        if(currentState.equals(State.SMALL)) {
            this.currentState = SUPER;
            this.score += 100;
        }
    }

    public void obtainCape() {
        if(currentState.equals(State.SMALL)
                || currentState.equals(SUPER)) {
            this.currentState = CAPE;
            this.score += 200;
        }
    }


    public void obtainFireFlower() {
        if(currentState.equals(State.SMALL)
            || currentState.equals(SUPER)) {
            this.currentState = FIRE;
            this.score += 300;
        }
    }

    public void meetMonster() {
        if(currentState.equals(SUPER)) {
            this.currentState = State.SMALL;
            this.score -= 100;
            return;
        }

        if(currentState.equals(CAPE)) {
            this.currentState = State.SMALL;
            this.score -= 200;
            return;
        }

        if(currentState.equals(FIRE)) {
            this.currentState = State.SMALL;
            this.score -= 300;
            return;
        }
    }
}


/**
 * 实现方式二：查表法
 *    /      GotMushRoom       GotCape       GotFireFlower   MetMonster
 *  Small    Super/+100       Cape/+200       Fire/+300           /
 *  Super         /           Cape/+200       Fire/+300      Small/-100
 *  Cape          /               /               /          Small/-200
 *  Fire          /               /               /          Small/-300
 */

enum Event {

    GOT_MUSHROOM(0),
    GOT_CAPE(1),
    GOT_FIRE(2),
    MET_MONSTER(3);

    private int value;

    private Event(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


class MarioStateMachine2 {

    private int score;
    private State currentState;

    public int getScore() {
        return score;
    }

    public State getCurrentState() {
        return currentState;
    }

    public MarioStateMachine2() {
        this.score = 0;
        this.currentState = State.SMALL;
    }

    private static final State[][] transitionTable = {
            {SUPER, CAPE, FIRE, SMALL},
            {SUPER, CAPE, FIRE, SMALL},
            {CAPE, CAPE, CAPE, SMALL},
            {FIRE, FIRE, FIRE, SMALL}
    };

    private static final int[][] actionTable = {
            {+100, +200, +300, +0},
            {+0, +200, +300, -100},
            {+0, +0, +0, -200},
            {+0, +0, +0, -300}
    };

    void obtainMushRoom() {
        executeEvent(Event.GOT_MUSHROOM);
    }

    void obtainCape() {
        executeEvent(Event.GOT_CAPE);
    }

    void obtainFireFlower() {
        executeEvent(Event.GOT_FIRE);
    }

    void meetMonster() {
        executeEvent(Event.MET_MONSTER);
    }

    void executeEvent(Event event) {
        int stateValue = currentState.getValue();
        int eventValue = event.getValue();

        this.currentState = transitionTable[stateValue][eventValue]; // 状态转移
        this.score += actionTable[stateValue][eventValue];           // 执行动作
    }
}


/**
 * 实现方式三：状态模式
 * 如果事件触发的动作比较复杂, 查表法的实现方式有一定的局限性
 */

interface IMario {     // 状态接口
    State getName();
    void obtainMushRoom();
    void obtainCape();
    void obtainFireFlower();
    void meetMonster();
}

class SmallMario implements IMario {

    private MarioStateMachine3 stateMachine;

    public SmallMario(MarioStateMachine3 stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public State getName() {
        return SMALL;
    }

    @Override
    public void obtainMushRoom() {
        stateMachine.setCurrentState(new SuperMario(stateMachine));
        stateMachine.setScore(stateMachine.getScore() + 100);
    }

    @Override
    public void obtainCape() {
        stateMachine.setCurrentState(new CapeMario(stateMachine));
        stateMachine.setScore(stateMachine.getScore() + 200);
    }

    @Override
    public void obtainFireFlower() {
        stateMachine.setCurrentState(new FireMario(stateMachine));
        stateMachine.setScore(stateMachine.getScore() + 300);
    }


    @Override
    public void meetMonster() {
        // do nothing
    }
}

class SuperMario implements IMario {

    private MarioStateMachine3 stateMachine;

    public SuperMario(MarioStateMachine3 stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public State getName() {
        return SUPER;
    }

    @Override
    public void obtainMushRoom() {}

    @Override
    public void obtainCape() {}

    @Override
    public void obtainFireFlower() {}

    @Override
    public void meetMonster() {}
    // ...
}

class FireMario implements IMario {

    private MarioStateMachine3 stateMachine;

    public FireMario(MarioStateMachine3 stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public State getName() {
        return FIRE;
    }

    @Override
    public void obtainMushRoom() {}

    @Override
    public void obtainCape() {}

    @Override
    public void obtainFireFlower() {}

    @Override
    public void meetMonster() {}
    // ...
}

class CapeMario implements IMario {

    private MarioStateMachine3 stateMachine;

    public CapeMario(MarioStateMachine3 stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public State getName() {
        return CAPE;
    }

    @Override
    public void obtainMushRoom() {}

    @Override
    public void obtainCape() {}

    @Override
    public void obtainFireFlower() {}

    @Override
    public void meetMonster() {}
}

class MarioStateMachine3 {

    private int score;
    private IMario currentState;

    public MarioStateMachine3() {
        this.score = 0;
        this.currentState = new SmallMario(this);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public IMario getCurrentState() {
        return currentState;
    }

    public void setCurrentState(IMario currentState) {
        this.currentState = currentState;
    }

    public void obtainMushRoom() {
        this.currentState.obtainMushRoom();
    }

    public void obtainCape() {
        this.currentState.obtainCape();
    }

    public void obtainFireFlower() {
        this.currentState.obtainFireFlower();
    }

    public void meetMonster() {
        this.currentState.meetMonster();
    }
}