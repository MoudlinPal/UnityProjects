using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI; // for Text


public class CarScript : MonoBehaviour {

    WheelJoint2D[] wheelJoints;
    JointMotor2D frontWheel;
    JointMotor2D backWheel;

    public float wheelSize = 0.39f;

    public float maxSpeed = -1000f;
    private float maxBackSpeed = 1500f;

    private float acceleration = 400f;
    private float decceleration = -100f;

    public float brakeForce = 3000f; // sila tormozheniya
    private float gravity = 9.8f;
    private float angleCar = 0; 

    public ClickScript[] controlCar;

    public bool grounded = false;
    public LayerMask map;
    public Transform bwheel;

    public Text coinsText;


    //////////////////////////////////////////
    public int coinsCount = 0; // amount of coins
    //////////////////////////////////////////

    public GameObject finishPanel;


    // Use this for initialization
    void Start () {
        wheelJoints = gameObject.GetComponents<WheelJoint2D>();
        backWheel = wheelJoints[0].motor; 
        frontWheel = wheelJoints[1].motor;
    }

    // Update is called once per frame
    void Update()
    {
        if(finishPanel.activeSelf != true) coinsText.text = (PlayerPrefs.GetInt("coinsCount") + coinsCount).ToString(); //coinsCount.ToString();

        grounded = Physics2D.OverlapCircle(bwheel.transform.position, wheelSize, map);
    }


    void FixedUpdate() {

        frontWheel.motorSpeed = backWheel.motorSpeed;


        angleCar = transform.localEulerAngles.z;

        if (angleCar >= 180)
        {
            angleCar -= 360;
        }

        if (grounded == true)
        { 

            if (controlCar[0].clickedIs == true) // gas!!!
            {
                backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed - (acceleration - gravity * Mathf.PI * (angleCar / 2)) * Time.deltaTime,
                    maxSpeed, maxBackSpeed); // ограничитель между MAX & MIN speed
            }

            //Тяготение машины гравитацией.
            if ((controlCar[0].clickedIs == false && backWheel.motorSpeed < 0) ||
                (controlCar[0].clickedIs == false && backWheel.motorSpeed == 0 && angleCar < 0)) // dont cliked and
            {
                backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed - (decceleration - gravity * Mathf.PI * (angleCar / 2)) * Time.deltaTime,
                    maxSpeed, 0);
            }
            else if ((controlCar[0].clickedIs == false && backWheel.motorSpeed > 0) ||
                (controlCar[0].clickedIs == false && backWheel.motorSpeed == 0 && angleCar > 0)) // если ещё есть тяга
            {
                backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed - (-decceleration - gravity * Mathf.PI * (angleCar / 2)) * Time.deltaTime,
                    0, maxBackSpeed);
            }
        }


        // если не на земле, чтобы скорость вращение колес постепено уывала если на зажат газ
        else if(controlCar[0].clickedIs == false && backWheel.motorSpeed < 0)
        {
            backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed - decceleration * Time.deltaTime, maxSpeed, 0);
        }
        else if (controlCar[0].clickedIs == false && backWheel.motorSpeed > 0)
        {
            backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed + decceleration * Time.deltaTime, 0, maxBackSpeed);
        }


        // если не касаемся земли, то можем газовать
        if (controlCar[0].clickedIs == true && grounded == false)
        {
            backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed - (acceleration - gravity * Mathf.PI * (angleCar / 2)) * Time.deltaTime,
                    maxSpeed, maxBackSpeed); // ограничитель между MAX & MIN speed
        }




        // brake
        if (controlCar[1].clickedIs == true && backWheel.motorSpeed > 0)
        {
            backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed - brakeForce * Time.deltaTime, 0, maxBackSpeed);
        }
        else if (controlCar[1].clickedIs == true && backWheel.motorSpeed < 0)
        {
            backWheel.motorSpeed = Mathf.Clamp(backWheel.motorSpeed + brakeForce * Time.deltaTime, maxSpeed, 0);
        }

        wheelJoints[1].motor = frontWheel;
        wheelJoints[0].motor = backWheel;

    }

    // чтобы отрисовывать Transform bwheel
    /*void OnDrawGizmos()
    {
        Gizmos.color = Color.red;
        Gizmos.DrawWireSphere(bwheel.transform.position,wheelSize);
    }*/



    void OnTriggerEnter2D(Collider2D trigger)
    {
        if (trigger.gameObject.tag == "Coin")
        {
            coinsCount++;
            Destroy(trigger.gameObject);
        }
        else if (trigger.gameObject.tag == "Finish")
        {
            finishPanel.SetActive(true);
            

            //////////////////////////////////////////////  save  ////////////////////////////////////////////////////////////////
            PlayerPrefs.SetInt("coinsCount", (coinsCount+ PlayerPrefs.GetInt("coinsCount"))); // save coins amount (cuurent + last)
            PlayerPrefs.Save(); 
        }
    }

}
