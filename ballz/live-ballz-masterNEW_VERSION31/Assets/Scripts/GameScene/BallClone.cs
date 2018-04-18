using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityScript;

public class BallClone : MonoBehaviour {


    private bool BALL_IS_MOVING_TO_NEW_START_POS;


    private void Start()
    {
        BALL_IS_MOVING_TO_NEW_START_POS = false;
    }

    

    private void Update()
    {
        if (BALL_IS_MOVING_TO_NEW_START_POS && transform.position != Ball.Instance.ballNewStartPosition.transform.position)
        {
            transform.position = Vector2.MoveTowards(transform.position, Ball.Instance.ballNewStartPosition.transform.position, Time.deltaTime* 4);
         
        }
        else if (BALL_IS_MOVING_TO_NEW_START_POS && transform.position == Ball.Instance.ballNewStartPosition.transform.position)
        {
            BALL_IS_MOVING_TO_NEW_START_POS = false;
            Destroy(gameObject);
        }



        if (Ball.Instance.COLLECT_BALL_TO_NEW_START_POS && transform.position != Ball.Instance.ballNewStartPosition.transform.position)
        {
            transform.position = Vector2.MoveTowards(transform.position, Ball.Instance.ballNewStartPosition.transform.position, Time.deltaTime * 6);
        }
        else if (Ball.Instance.COLLECT_BALL_TO_NEW_START_POS && transform.position == Ball.Instance.ballNewStartPosition.transform.position)
        {
            Destroy(gameObject);        
        }


        
    }




  



    // handling collisions of the ball with the others objects
    void OnCollisionEnter2D(Collision2D coll)
    {
        if (coll.gameObject.tag == "Floor")      // floor
        {
            BALL_IS_MOVING_TO_NEW_START_POS = true;
            Ball.Instance.TouchFloor(this.gameObject);
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
            Ball.Instance.amountBalls++;
        }
    }




}
