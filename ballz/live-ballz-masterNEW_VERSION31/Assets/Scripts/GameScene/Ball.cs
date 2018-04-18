﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Ball : MonoSingleton<Ball>
{

    public GameObject ballPrefab;
    public GameObject ballLastStartPosition;
    public GameObject ballNewStartPosition;



    public Text scoreText;
    public Text amountBallsText;
    public Text bestScoreText;


    //private const float DEADZONE = 30.0f;
    //private const float MAXIMUM_PULL = 50.0f;


    public bool isBreakingStuff;
    public bool firstBallLanded;
    public bool allBallLanded;


    private Vector2 landingPosition;



    private Rigidbody2D rigid;


    public int amountBalls = 9;
    public int amountBallsLeft = 0;
    public int amountBallsReleased = 0;




    public float speed = 5.5f;


    public Transform ballsPreview;
    public GameObject tutorialContainer;

    public GameObject returnButton;
    private Vector3 rButtonStartPosition;
    private Vector3 rButtonEndPosition;
    private bool RBUTTON_IS_MOVING;

    //public Transform rowContainer;
    //public GameObject rowPrefab;


    private float currentSpawnY;
    public int score;



    public GameObject timeScalePanel;
    private float timeToAccelerate;


    public bool COLLECT_BALL_TO_NEW_START_POS;


    

    private void Start()
    {

        bestScoreText.text = PlayerPrefs.GetInt("Score").ToString();
        

        COLLECT_BALL_TO_NEW_START_POS = false;

        timeToAccelerate = 0.0f;

        if (PlayerPrefs.GetInt("Score") > 1)
        {
            score = PlayerPrefs.GetInt("Score");
            amountBalls = PlayerPrefs.GetInt("Score") - 1;
        }
        else
        {
            score = 1;
            amountBalls = 1;
        }

        rButtonEndPosition = returnButton.transform.position;
        rButtonStartPosition = new Vector3(returnButton.transform.position.x, returnButton.transform.position.y - 1.0f, 0.0f);
        returnButton.transform.position = rButtonStartPosition;


        rigid = GetComponent<Rigidbody2D>();
        ballsPreview.parent.gameObject.SetActive(false);

        amountBallsLeft = amountBalls;
        amountBallsReleased = amountBalls;

        UpdateTextAndSaveScore();


        BlockContainer.Instance.GenerateNewRow();
    }

    



    private void Update()
    {
        if (!PauseMenu.Instance.isPause)
        {

            if (!isBreakingStuff)
            {
                timeScalePanel.SetActive(false);
                Time.timeScale = 1.0f;
                timeToAccelerate = 0.0f;
                PoolInput();
            }

            // roll out ReturnButton
            else if (RBUTTON_IS_MOVING && returnButton.transform.position != rButtonEndPosition)
            {
                // Moving the ReturnButton
                returnButton.transform.position = Vector2.MoveTowards(
                    returnButton.transform.position,
                    rButtonEndPosition,
                       Time.deltaTime * (2.5f + Time.deltaTime));
            }


            // Accelerate our TimeScale
            if (isBreakingStuff)
            {
                timeToAccelerate += Time.deltaTime;
            }
            if (timeToAccelerate > 7.0f)
            {
                timeScalePanel.SetActive(true);
                Time.timeScale = 2.0f;
            }





            if (GameObject.FindGameObjectsWithTag("Ball").Length == 1 && COLLECT_BALL_TO_NEW_START_POS)
            {
                COLLECT_BALL_TO_NEW_START_POS = false;
                AllBallLanded();
            }

        }
    }


 

    // Drag the ball around, visualize the trajectory
    private void PoolInput()
    {
        Vector3 sd = MobileInput.Instance.swipeDelta;
        sd.Set(sd.x, sd.y, sd.z);
        
        if (sd != Vector3.zero)
        {
            // Are we draggin in the wrong direction?
            // if (sd.y < 30)
            //{
            //    sd.y = 30;
            //    ballsPreview.parent.gameObject.SetActive(false);
            //}
            //else
            //{

              
                ballsPreview.parent.up = sd.normalized;
                ballsPreview.parent.gameObject.SetActive(true);

                //Плавное "скольжение" из одной точки в другую
                //ballsPreview.localScale = Vector3.Lerp(new Vector3(1, 1, 1), new Vector3(1, 3, 1), sd.magnitude / MAXIMUM_PULL);



                LineRender.Instance.BollIsrelease = true; // линия отображается



                // Hide Button
                RBUTTON_IS_MOVING = false;
                returnButton.transform.position = rButtonStartPosition;


                if (MobileInput.Instance.release)
                {
                    if(amountBalls > 1)
                        RBUTTON_IS_MOVING = true;

                    allBallLanded = false;

                    tutorialContainer.SetActive(false);

                    isBreakingStuff = true;             // turn off this function


                    SendBallInDirection(sd.normalized); // release of ball



                    ballsPreview.parent.gameObject.SetActive(false);
                    //amountBallsText.gameObject.SetActive(false);
                    rigid.simulated = true;



                    LineRender.Instance.BollIsrelease = false;// линия не отображается

                }
            //}
        }
    }







    private void SendBallInDirection(Vector3 dir)
    {
        Vector3 startPos = ballNewStartPosition.transform.position;
        ballLastStartPosition.transform.position = startPos;


        amountBallsLeft = amountBalls;
        amountBallsReleased = amountBalls;


        if (amountBallsReleased > 0)
        {
            amountBallsReleased--;
            amountBallsText.text = 'X' + amountBallsReleased.ToString();
            if (amountBallsReleased == 0)
                amountBallsText.text = "";
        }
        else
        {
            amountBallsText.text = "";
        }

        if(amountBalls == 1)
        {
            rigid.velocity = dir * speed; // spawn THE FIRST BALL
        }


        for (int i = 1; i <= (1 * amountBalls)-1; i++) // Spawning balls 
        {
            if(i == 1)
            {
               rigid.velocity = dir * speed; // spawn THE FIRST BALL
            }
            StartCoroutine(SpawnBallInDirection(startPos, dir, 0.1f * i));
        }

    }

    private IEnumerator SpawnBallInDirection(Vector3 startPosition, Vector3 dir, float time)
    {
        yield return new WaitForSeconds(time);
        

        if (amountBallsReleased > 0)
        {
            amountBallsReleased--;
            amountBallsText.text = 'X' + amountBallsReleased.ToString();
            if(amountBallsReleased == 0)
                amountBallsText.text = "";
        }
        else
        {
            amountBallsText.text = "";
        }


        GameObject newBall = Instantiate(ballPrefab) as GameObject;

        newBall.transform.position = startPosition;

        Rigidbody2D rigidNewBall = newBall.GetComponent<Rigidbody2D>();
        rigidNewBall.simulated = true;
        rigidNewBall.velocity = dir * speed;
        
    }







    public void TouchFloor(GameObject ball)
    {

        amountBallsLeft--;

        if (ball.Equals(gameObject)) // this is our MAIN BALL
        {
            // stop the Ball
            Rigidbody2D rigid = ball.GetComponent<Rigidbody2D>(); ;
            rigid.velocity = Vector2.zero;
            rigid.simulated = false;


            if (!firstBallLanded)
            {
                firstBallLanded = true;
                ballNewStartPosition.transform.position = new Vector3(ball.transform.position.x, ballLastStartPosition.transform.position.y, 0.0f);
                ballLastStartPosition.transform.position = new Vector3(ballNewStartPosition.transform.position.x, ballNewStartPosition.transform.position.y, 0.0f);
            }


            // Set MAIN Ball position
            ball.transform.position = new Vector3(ballNewStartPosition.transform.position.x, ballNewStartPosition.transform.position.y, 0.0f);
        }
        else if (!firstBallLanded) // work with Ball Clone
        {
            firstBallLanded = true;

            ballNewStartPosition.transform.position = new Vector3(ball.transform.position.x, ballLastStartPosition.transform.position.y, 0.0f);
            ballLastStartPosition.transform.position = new Vector3(ballNewStartPosition.transform.position.x, ballNewStartPosition.transform.position.y, 0.0f);

            //Destroy(ball);
            StopBall(ball);
            ball.transform.position = new Vector3(ball.transform.position.x, ballLastStartPosition.transform.position.y, 0.0f);
        }
        else
        {
            //Destroy(ball);
            StopBall(ball);
            ball.transform.position = new Vector3(ball.transform.position.x, ballLastStartPosition.transform.position.y, 0.0f);
        }

        if (amountBallsLeft == 0)
            AllBallLanded();
    }








    // When all of balles are landed
    public void AllBallLanded()
    {
        // Hide RButton
        RBUTTON_IS_MOVING = false;
        returnButton.transform.position = rButtonStartPosition;
       


            allBallLanded = true;
            isBreakingStuff = false;
            firstBallLanded = false;




            BlockContainer.Instance.MoveOldRow();




            score++;
            UpdateTextAndSaveScore();
            //amountBallsText.gameObject.SetActive(true);

            amountBallsLeft = amountBalls;
            amountBallsReleased = amountBalls;

        
       
    }









    public void UpdateTextAndSaveScore()
    {
        scoreText.text = score.ToString();
        amountBallsText.text = 'X' + amountBalls.ToString();

        if (PlayerPrefs.GetInt("Score") < score)
        {
            PlayerPrefs.SetInt("Score", score);
            bestScoreText.text = score.ToString();
        }
    }




    public void CollectBall()
    {
        //amountBalls++;
        Debug.Log(amountBalls);
    }







    public void ReturnBalls()
    {
        if (!PauseMenu.Instance.isPause)
        {
            if (isBreakingStuff) // запещуены шарики и ломаем кубики
            {
                StopAllCoroutines(); // Stop spawning of others Balls

                GameObject[] objects = GameObject.FindGameObjectsWithTag("Ball");

                foreach (GameObject ball in objects) // select every Ball on the Scene
                {
                    // stop the Ball
                    StopBall(ball);


                    // move the Ball to the next start POSITION
                    //ball.transform.position = Vector2.MoveTowards(
                    //ball.transform.position,
                    //transform.position,
                    //   3);



                    //if (!ball.Equals(gameObject)) // if this is our Clone Ball => destroy it
                    //    Destroy(ball);



                }

                if (amountBalls > 1)
                    COLLECT_BALL_TO_NEW_START_POS = true;

                if (amountBalls == 1)
                    AllBallLanded();

                // Set MAIN Ball position
                gameObject.transform.position = ballNewStartPosition.transform.position;

            }
        }
    }
    







   
    public void StopBall(GameObject ball)
    {
        // stop the Ball
        Rigidbody2D rigid = ball.GetComponent<Rigidbody2D>(); ;
        rigid.velocity = Vector2.zero;
        rigid.simulated = false;
    }





  








    // handling collisions of the ball with the others objects
    void OnCollisionEnter2D(Collision2D coll)
    {
        if (coll.gameObject.tag == "Floor")      // floor
        {
            TouchFloor(this.gameObject);
        }
        else if (coll.gameObject.tag == "Block") // block
        {
            coll.transform.parent.SendMessage("ReceiveHit");
        }
        
    }



    void OnTriggerEnter2D(Collider2D coll)
    {
        if (coll.gameObject.tag == "GetBall") // destroy GetBall and get +1 ball
        {
            coll.transform.parent.SendMessage("DestroyGetBall");
            amountBalls++;
        }
    }


}
