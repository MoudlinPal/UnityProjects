using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MobileInput : MonoSingleton<MobileInput>
{

    public bool tap,release,hold;
    public Vector2 swipeDelta;


    


    private Vector2 initialPosition;



    




    private void Update()
    {
        if (!PauseMenu.Instance.isPause)
        {
            release = tap = false;
            swipeDelta = Vector2.zero;





            if (Camera.main.ScreenToWorldPoint(Input.mousePosition).y > LineRender.Instance.cancelLinesPanel.transform.position.y
                && Camera.main.ScreenToWorldPoint(Input.mousePosition).y < LineRender.Instance.cancelDrawLinesPanelRoof.transform.position.y)
            {

                if (Input.GetMouseButtonDown(0))
                {
                    initialPosition = Input.mousePosition;
                    hold = tap = true;
                }
                else if (Input.GetMouseButtonUp(0))
                {
                    release = true;
                    hold = false;
                    swipeDelta = (Vector2)Input.mousePosition - initialPosition;
                }


                if (hold)
                    swipeDelta = (Vector2)Input.mousePosition - initialPosition;

            }




        }
    }











   








}
