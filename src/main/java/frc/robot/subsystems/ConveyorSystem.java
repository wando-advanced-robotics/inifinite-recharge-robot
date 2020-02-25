package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * Conveyor System represents the conveyor system of the robot.
 */
public class ConveyorSystem extends SubsystemBase {
	/**
	 * The DIO channel for the left ready sensor.
	 */
	private static final int READY_LEFT = 0;

	/**
	 * The DIO channel for the center ready sensor.
	 */
	private static final int READY_CENTER = 1;

	/**
	 * The DIO channel for the right ready sensor.
	 */
	private static final int READY_RIGHT = 2;

	/**
	 * The DIO channel for the full sensor.
	 */
	private static final int FULL = 3;

	/**
	 * The default value used to drive the conveyor motor.
	 */
	private final static double DEFAULT_CONVEYOR_MOTOR_OUTPUT = 0.5;

	/**
	 * The size of a single step of the conveyor.
	 */
	private final static int STEP_SIZE = 4096 * 5;

	/**
	 * The motor that drives the conveyor.
	 */
	private WPI_TalonSRX motor = null;

	/**
	 * A flag to indicate whether the conveyor is active or not.
	 */
	private boolean isActive = false;

	/**
	 * The ready sensors that determine if a power cell is ready to be ingested.
	 */
	private DigitalInput readyLeft = new DigitalInput(READY_LEFT);
	private DigitalInput readyCenter = new DigitalInput(READY_CENTER);
	private DigitalInput readyRight = new DigitalInput(READY_RIGHT);

	/**
	 * full is the sensor that determines if the conveyor is full and cannot
	 * receive any further power cells.
	 */
	private DigitalInput full = new DigitalInput(FULL);

	/**
	 * Create a new conveyor system.
	 */
	public ConveyorSystem() {
		this.motor = new WPI_TalonSRX(Constants.CONVEYOR_MOTOR);

		this.motor.configSelectedFeedbackSensor(
			FeedbackDevice.CTRE_MagEncoder_Absolute,
			0,
			30
		);

		this.motor.config_kP(0, 1.0, 100);
		this.motor.config_kI(0, 0.0, 100);
		this.motor.config_kD(0, 0.0, 100);
		this.motor.config_kF(0, 0.0, 100);

		this.motor.setInverted(InvertType.InvertMotorOutput);

		this.motor.configPeakOutputForward(1.0);
		this.motor.configPeakOutputReverse(-1.0);

		this.motor.configNominalOutputForward(0.0);
		this.motor.configNominalOutputReverse(0.0);
	}

	/**
	 * Move the conveyor in the forward direction.
	 *
	 * The conveyor will move at the rate specified by CONVEYOR_MOTOR_PERCENT.
	 */
	public void forward(){
		this.forward(DEFAULT_CONVEYOR_MOTOR_OUTPUT);
	}

	/**
	 * Move the conveyor in the forward direction.
	 *
	 * @param percent the percent output the motor driving the conveyor.
	 */
	public void forward(double percent) {
		this.isActive = true;
		this.motor.set(ControlMode.PercentOutput, percent);
	}

	/**
	 * Empty the contents of the conveyor.
	 */
	public void empty() {
		this.forward(1.0);
	}

	/**
	 * Move the conveyor in the reverse direction.
	 */
	public void reverse() {
		this.reverse(-DEFAULT_CONVEYOR_MOTOR_OUTPUT);
	}

	/**
	 * Move the conveyor in the reverse direction.
	 *
	 * @param percent the percent output of the motor driving the conveyor.
	 */
	public void reverse(double percent) {
		this.isActive = true;
		this.motor.set(ControlMode.PercentOutput, percent);
	}

	/**
	 * Stop the conveyor.
	 */
	public void stop() {
		this.isActive = false;
		this.motor.stopMotor();
	}

	/**
	 * Advance the conveyor by the configured step size.
	 */
	public void step() {
		this.isActive = true;
		this.motor.set(ControlMode.Position, STEP_SIZE);
	}

	/**
	 * Reset the current step position.
	 */
	public void reset() {
		this.motor.setSelectedSensorPosition(0);
	}

	/**
	 * Returns whether a power cell is ready to be ingested.
	 *
	 * @return <code>true</code> if a power is ready to be ingested, otherwise
	 * <code>false</code>.
	 */
	public boolean ready() {
		boolean ready = !this.readyLeft.get()
					 || !this.readyCenter.get()
					 || !this.readyRight.get();

		return ready;
	}

	public int getPosition() {
		return this.motor.getSelectedSensorPosition();
	}

	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * Returns whether the step is complete.
	 *
	 * The step is considered to be complete if the step size has been reached
	 * or if the conveyor is full.
	 *
	 * @return <code>true</code> if the step is complete, otherwise
	 * <code>false</code>.
	 */
	public boolean isStepComplete() {
		int position = this.motor.getSelectedSensorPosition();

		return position >= STEP_SIZE; //|| !this.full.get();
	}
}