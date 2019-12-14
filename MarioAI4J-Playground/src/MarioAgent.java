import java.awt.Graphics;
import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.IAgent;
import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.VisualizationComponent;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.utils.MarioLog;

public class MarioAgent extends MarioHijackAIBase implements IAgent {
	@Override
	public void reset(AgentOptions options) {
		super.reset(options);
	}

	private boolean dangerdown() {
		return
				e.danger(1, 3) |e.danger(1, 2)|| e.danger(3, 5);
	}

	private boolean brickAhead() {
		return
				t.brick(1, 0) || t.brick(2, 0) || t.brick(3, 0) ||
						t.brick(1, -1) || t.brick(2, -1)|| t.brick(3, -1);
	}

	private boolean enemyAhead() {
		return
				e.danger(0, 0) || e.danger(1, 0)      ||
						(e.danger(1,1)  && !t.brick(1, 1))    ||
						(e.danger(1,2)  && !t.brick(1, 2))    ||
						(e.danger(2, 3) && !t.brick(2, 3))    ||
						(e.danger(3, 3) && !t.brick(3, 3))    ||
						//o//
						//(e.danger(4, 0) && e.squishy(8, -2))  ||
						(e.danger(4, 0) && e.squishy(8, 0))   ||
						//(e.danger(4, 1) && !t.brick(4, 1))   || //recently added
						//'//
						(e.danger(2, 0) && !e.squishy(8, 0))  ||
						(e.danger(2,1)  && !t.brick(2, 1))    ||
						(e.danger(2,2)  && !t.brick(2, 2))    ||
						(e.danger(1, 3) && !t.brick(1, 3));
	}

	@Override
	public void debugDraw(VisualizationComponent vis, LevelScene level,	IEnvironment env, Graphics g) {
		super.debugDraw(vis, level, env, g);
		if (mario == null) return;

		String debug = "MY DEBUG STRING";
		VisualizationComponent.drawStringDropShadow(g, debug, 0, 26, 1);
	}


	public MarioInput actionSelectionAI() { {

		control.runRight();
		control.shoot();
		if ((enemyAhead() || brickAhead()))
		{
			control.jump();
		}
		if (!mario.onGround) {
			control.jump();
			if (dangerdown())
			{
				control.sprint();
			}
		}

	}

		return action;}
	public static void main(String[] args) {

		LevelConfig level = LevelConfig.LEVEL_4_SPIKIES;

		//MarioSimulator simulator = new MarioSimulator(level.getOptions());
		MarioSimulator simulator = new MarioSimulator(level.getOptionsRandomized());
		IAgent agent = new MarioAgent();

		EvaluationInfo info = simulator.run(agent);

		switch (info.getResult()) {
			case LEVEL_TIMEDOUT:
				MarioLog.warn("LEVEL TIMED OUT!");
				break;

			case MARIO_DIED:
				MarioLog.warn("MARIO KILLED");
				break;

			case SIMULATION_RUNNING:
				MarioLog.error("SIMULATION STILL RUNNING?");
				throw new RuntimeException("Invalid evaluation info state, simulation should not be running.");

			case VICTORY:
				MarioLog.warn("VICTORY!!!");
				break;
		}
	}

}