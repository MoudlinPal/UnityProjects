using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;


public class GarageInterface : MonoBehaviour {

    public GameObject impInterface;
    private bool alternFuncChooseCar = false;
    private bool funcChooseCar = false;
    public Scrollbar scrollBar;

    public GameObject[] CarsObjects;
    private SpriteRenderer[] wheels;
    

    public Button[] improveBttns;
    private string[] tuning1, tuning2; // for each car !!!!!!!!!!!!!!!!!!
    private char del = '9'; // to delete 9

    void Awake() // completing first
    {
        /*  if(!PlayerPrefs.HasKey("Imp1")) // if there's no key "Improvement1"
          {               // then create new key
              PlayerPrefs.SetString("Imp1", "09090"); // 0 - прокачка сама
              PlayerPrefs.Save();
          }
          tuning1 = PlayerPrefs.GetString("Imp1").Split(del); 

          // if MAX improvement then block Button
          for(int i = 0; i < tuning1[i].Length; i++)
          {
              if (int.Parse(tuning1[i]) == 3) // 3 - max update of term
              {
                  improveBttns[i].interactable = false; // block Button
              }
          }*/

        /*
        for (int i = 0; i < CarsObjects.Length; i++) // for start = all cars black exept saveCar
        {
            CarsObjects[i].GetComponent<SpriteRenderer>().color = Color.black; // native color = white
            wheels = CarsObjects[i].GetComponentsInChildren<SpriteRenderer>();
            wheels[1].color = Color.black; wheels[2].color = Color.black;
        }
        // then draw last choozen CAR
        int lastCar = PlayerPrefs.GetInt("car");
        CarsObjects[lastCar].GetComponent<SpriteRenderer>().color = Color.white;
        wheels = CarsObjects[lastCar].GetComponentsInChildren<SpriteRenderer>();
        wheels[1].color = Color.white; wheels[2].color = Color.white;
        */

        chooseCar(PlayerPrefs.GetInt("car"));

      
    }


    void Start()
    {
        /////// scroll to last choosen car ////////
        switch (PlayerPrefs.GetInt("car"))
        {
            case 0: scrollBar.value = 0.00f; break;
            case 1: scrollBar.value = 1.00f; break;
        }

    }


    void Update()
    {
        ///////////////////////////////// scrollView ////////////////////////////////
        /////// Scroll View for choosen car
        //!!if (funcChooseCar == false)
        //!!{
        if (Input.GetKey(KeyCode.Mouse0) == false)
            {
                if (scrollBar.value < 0.5f)
                {
                    scrollBar.value -= 2f * Time.deltaTime; 
                }
                else if (scrollBar.value < 1.0f)
                {
                    scrollBar.value += 2f * Time.deltaTime;
                }
            }
        //!!}

        ////////// scroll to choosen car (after functin chooseCar) /////////////
        //!!if (funcChooseCar == true)
        //!!{
        if (Input.GetKey(KeyCode.Mouse0) == false)
            {
                if (PlayerPrefs.GetInt("car") == 0)
                {
                    scrollBar.value -= 5f * Time.deltaTime;
                    //if(scrollBar.value == 0) funcChooseCar = false;
                }
                else if (PlayerPrefs.GetInt("car") == 1)
                {
                    scrollBar.value += 5f * Time.deltaTime;
                    //if (scrollBar.value == 1) funcChooseCar = false;
                }
            }
            if (scrollBar.value == 0.00f || scrollBar.value == 1.00f) funcChooseCar = false;
        //!!}
        //!!else
        //!!{
        if (scrollBar.value < 0.5f && PlayerPrefs.GetInt("car") == 1) { alternFuncChooseCar = true; chooseCar(0); }
            else if (scrollBar.value > 0.5f && PlayerPrefs.GetInt("car") == 0) { alternFuncChooseCar = true; chooseCar(1); }
        //!!}


        if (scrollBar.value == 0 || scrollBar.value == 1)
        {
            impInterface.SetActive(true);
        }
        else
        {
            impInterface.SetActive(false);
        }


        if (Input.GetKeyDown(KeyCode.Escape))
        {
            SceneManager.LoadScene("Menu");
        }
    }




    public void chooseCar(int numCar)
    {


        ////////////////////////////////////////  save choozen car  ///////////////////////////////////////////////
        PlayerPrefs.SetInt("car", numCar);
        PlayerPrefs.Save();

        for (int i = 0; i < CarsObjects.Length; i++) // blacking cars exept choozen
        {
            if (i != numCar)
            {
                CarsObjects[i].GetComponent<SpriteRenderer>().color = Color.black;
                wheels = CarsObjects[i].GetComponentsInChildren<SpriteRenderer>();
                wheels[1].color = Color.black; wheels[2].color = Color.black;
            }
        }

        /////// draw choozen car ////////
        CarsObjects[numCar].GetComponent<SpriteRenderer>().color = Color.white;
        wheels = CarsObjects[numCar].GetComponentsInChildren<SpriteRenderer>();
        wheels[1].color = Color.white; wheels[2].color = Color.white;

        if(!alternFuncChooseCar) funcChooseCar = true;
        
    }

  
}
