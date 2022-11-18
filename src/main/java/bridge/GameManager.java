package bridge;

import java.util.List;

public class GameManager {
    private final InputView input;
    private final OutputView output;
    private boolean newGame = true;
    private GameSession gameSession;
    BridgeGame game = new BridgeGame();

    public GameManager(InputView input, OutputView output) {
        this.input = input;
        this.output = output;
    }


    public void run() {
        while (newGame) {
            gameSequence();
        }
    }

    private void gameSequence() {
        List<String> bridge = initGame();

        while (!gameSession.isArrived()) {
            if (moveAndCheck(bridge)) break;
        }
    }

    private List<String> initGame() {
        output.printStart();
        output.printInputLength();
        int bridgeSize = input.readBridgeSize();
        BridgeMaker bridgeMaker = new BridgeMaker(new BridgeRandomNumberGenerator());
        List<String> bridge = bridgeMaker.makeBridge(bridgeSize);
        gameSession = new GameSession(bridge);
        return bridge;
    }

    private boolean moveAndCheck(List<String> bridge) {
        output.printInputChooseCell();
        String move = input.readMoving();
        game.move(gameSession, move);
        output.printMap(bridge, gameSession.getStep());
        if (checkMoveSucceed(bridge)) {
            return true;
        }
        if (checkArrival(bridge)) {
            return true;
        }
        return false;
    }

    private boolean checkArrival(List<String> bridge) {
        if (gameSession.isArrived()) {
            output.printChooseRetry();
            String command = input.readGameCommand();
            if (handleRetry(bridge, command)) {
                return true;
            }
            ;
        }
        return false;
    }

    private boolean handleRetry(List<String> bridge, String command) {
        boolean isRetry = game.retry(command, gameSession);
        if (ArriveAndQuit(bridge, isRetry)) {
            return true;
        }
        if (ArriveAndRetry(isRetry)) {
            return true;
        }
        return false;
    }

    private boolean ArriveAndRetry(boolean isRetry) {
        if (isRetry) {
            newGame = true;
            gameSession.clearStep();
            gameSession.clearTrial();
            return true;
        }
        return false;
    }

    private boolean ArriveAndQuit(List<String> bridge, boolean isRetry) {
        if (!isRetry) {
            output.printResult(bridge, gameSession, GameResult.succeed);
            newGame = false;
            return true;
        }
        return false;
    }

    private boolean checkMoveSucceed(List<String> bridge) {
        if (!gameSession.isMoveSuccess()) {
            output.printChooseRetry();
            String command = input.readGameCommand();
            boolean isRetry = game.retry(command, gameSession);
            if (failAndQuit(bridge, isRetry)) {
                return true;
            }
        }
        return false;
    }

    private boolean failAndQuit(List<String> bridge, boolean isRetry) {
        if (!isRetry) {
            output.printResult(bridge, gameSession, GameResult.fail);
            newGame = false;
            return true;
        }
        return false;
    }
}