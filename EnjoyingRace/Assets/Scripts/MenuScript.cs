using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class MenuScript : MonoBehaviour {

    public GameObject levelChanger;
    public GameObject exitPanel;
    //public GameObject garagePanel;
 
    //public Image[] cars;
    //public Button[] bttns;
    //public Text[] carsText;
    //public string[] levels;

    public Text coinsAmount;

    private void Awake()
    {
        
        //Screen.SetResolution(854, 480, true);
    }


    /*void Start()
    {

    

        //cars[PlayerPrefs.GetInt("car")].color = Color.white;

        //if (PlayerPrefs.GetInt(levels[0]) == 3) // if lvl comleted for 3 stars
        //{
        //    bttns[0].interactable = true;
        //    carsText[0].gameObject.SetActive(false);
        //}

    }*/

    void Update()
    {
        //if(garagePanel.activeSelf == true && Input.GetKeyDown(KeyCode.Escape))
        //{
        //    garagePanel.SetActive(false);
        //} else

        if(levelChanger.activeSelf == true && Input.GetKeyDown(KeyCode.Escape))
        {
            levelChanger.SetActive(false);
        }
        else if (exitPanel.activeSelf == false && Input.GetKeyDown(KeyCode.Escape))
        {
            exitPanel.SetActive(true);
        }
        else if(Input.GetKeyDown(KeyCode.Escape))
        {
            exitPanel.SetActive(false);
        }


        //garagePanel.activeSelf == false && 
        if (levelChanger.activeSelf == false && exitPanel.activeSelf == false) // display coins Amout
            coinsAmount.text = "Coins: " + PlayerPrefs.GetInt("coinsCount").ToString();
        else
            coinsAmount.text = "";



    }

    public void OnClickStart()
    {
        levelChanger.SetActive(true);
    }

    public void OnClickExit()
    {
        Application.Quit();
    }
    public void OnClickNo()
    {
        exitPanel.SetActive(false);
    }

    public void levelButtons(int level)
    {
        SceneManager.LoadScene(level);
    }


    public void ToGarage()
    {
        SceneManager.LoadScene("GarageDemo"); // "Garage"
    }

    

    //public void carChoice(int numCar)
    //{
    //    ////////////////////////////////////////  save choozen car  ///////////////////////////////////////////////
    //    PlayerPrefs.SetInt("car", numCar);
    //    PlayerPrefs.Save();

    //    for(int i = 0; i < cars.Length; i++)
    //    {
    //        cars[i].color = Color.black;
    //        cars[numCar].color = Color.white; // native color
    //    }
       
    //}
}
