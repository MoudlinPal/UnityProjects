using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class Interface : MonoBehaviour {

    public CarScript cs; // to TAKE STARS
    private SmoothCamera sc; // to take view for needed car

    public float Timer = 0f;
    public Text coinsCount;

    public float starTime = 8f; // after 8 sek star--
    private int starsAmount = 0; // for levelChangerPanel on MainMenu
    public Image[] stars;

    public GameObject[] cars;

    public string keyName = "starsLvl1";

    private bool isPaused = false;
    public GameObject pp; // pause panel
  


    void Start()
    {
        cars[PlayerPrefs.GetInt("car")].SetActive(true);

        cs = cars[PlayerPrefs.GetInt("car")].GetComponent<CarScript>();

        sc = GetComponent<SmoothCamera>();
        sc.target = cars[PlayerPrefs.GetInt("car")].transform;
        sc.sc = cars[PlayerPrefs.GetInt("car")].GetComponent<WheelJoint2D>();
    }


    void Update() {

        if (cs.finishPanel.activeSelf == true)
        {
            
            for (int i = 0; i < cs.controlCar.Length; i++) // block control Car buttons (чтобы не работал зажим газа после финиша)
            {
                cs.controlCar[i].clickedIs = false;
                cs.controlCar[i].gameObject.SetActive(false);
            }

            coinsCount.text = "Coins: " + cs.coinsCount.ToString();
            if(Input.GetMouseButtonDown(0)) // - left Button on mouse and to MainMenu
            {
                SceneManager.LoadScene(0);
            }

            if (Timer <= starTime) // add Stars for WIN
            {

                stars[0].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
                stars[1].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
                stars[2].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);

                starsAmount = 3;
                //////////////////////////////////////////////  save  ////////////////////////////////////////////////////////////////
                PlayerPrefs.SetInt(keyName, starsAmount); // система сохранения данных (сохраняется в реестре)
                PlayerPrefs.Save(); // сохраняем изменения
            }
            else if (Timer <= starTime * 2) // add Stars for WIN
            {
                stars[0].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
                stars[1].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
                if (PlayerPrefs.GetInt(keyName) != 3)
                {
                    starsAmount = 2;
                    //////////////////////////////////////////////  save  ////////////////////////////////////////////////////////////////
                    PlayerPrefs.SetInt(keyName, starsAmount); // система сохранения данных (сохраняется в реестре)
                    PlayerPrefs.Save(); // сохраняем изменения
                }
            }
            else if (Timer <= starTime * 3) // add Stars for WIN
            {
                stars[0].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
                if (PlayerPrefs.GetInt(keyName) != 3 && PlayerPrefs.GetInt(keyName) != 2)
                {
                    starsAmount = 1;
                    //////////////////////////////////////////////  save  ////////////////////////////////////////////////////////////////
                    PlayerPrefs.SetInt(keyName, starsAmount); // система сохранения данных (сохраняется в реестре)
                    PlayerPrefs.Save(); // сохраняем изменения
                }
            }

                
           

        }	
        else if(SceneManager.GetActiveScene().name == "Level1") // Timer
        {
            Timer += Time.deltaTime;
        }


        ///////////// PAUSE //////////////////
        if (!cs.finishPanel.activeSelf)
        {
            if (Input.GetKeyDown(KeyCode.Escape) && !isPaused)
            {
                pp.SetActive(true);
                Time.timeScale = 0; // Stop time
                isPaused = true;
            }
            else if (Input.GetKeyDown(KeyCode.Escape) && isPaused)
            {
                pp.SetActive(false);
                Time.timeScale = 1; // Run time
                isPaused = false;
            }
        }
        ///////////// PAUSE //////////////////
    }


    /////////// Button Pause and func. for PausePanel bttns //////////////

    public void OnPause() // Button Pause 
    {
        pp.SetActive(true);
        Time.timeScale = 0; // Stop time
        isPaused = true;
    }

    public void OnContinue()
    {
        pp.SetActive(false);
        Time.timeScale = 1; // Run time
        isPaused = false;
    }
    public void ToMenu()
    {
        Time.timeScale = 1; // Run time
        SceneManager.LoadScene("Menu");    
    }

}
