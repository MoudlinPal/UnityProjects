using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PauseMenu : MonoSingleton<PauseMenu>
{

    public bool isPause;


    public GameObject PausePanel;
    private Animator animatorPausePanelAppearance;


    private float previousTimeScale;



	void Start () {

        previousTimeScale = 1.0f;

        isPause = false;
        PausePanel.SetActive(false);

        animatorPausePanelAppearance = PausePanel.GetComponent<Animator>();
    }
	






	void Update () {
		
	}


    

    public void PauseOn()
    {
        if (!isPause)
        {
            previousTimeScale = Time.timeScale;
            isPause = true;
            //Time.timeScale = 0;
            PausePanel.SetActive(true);
            
            animatorPausePanelAppearance.Play("PausePanelAppearanceAnim", animatorPausePanelAppearance.GetLayerIndex("Base Layer"));
            StartCoroutine(StopTime());
        }
    }


    private IEnumerator StopTime()
    {
        yield return new WaitForSeconds(0.6f);

        Time.timeScale = 0.0f;
    }



    public void PauseOff()
    {
        if (isPause)
        {
            Time.timeScale = previousTimeScale;

            // HERE PLAY REVERSE ANIMATION
            animatorPausePanelAppearance.Play("PausePanelDisappearanceAnim", animatorPausePanelAppearance.GetLayerIndex("Base Layer"));


            StartCoroutine(HidePausePanel());

           
        }
    }



    private IEnumerator HidePausePanel()
    {
        yield return new WaitForSeconds(0.5f);
        
        PausePanel.SetActive(false);
        isPause = false;
    }



}
