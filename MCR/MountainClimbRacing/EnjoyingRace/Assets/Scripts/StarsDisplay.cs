using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class StarsDisplay : MonoBehaviour {


    private Image[] stars;
    public Button[] bttns;
    private MenuScript ms;
    public int levelChanger = 0;
    public string keyName = "starsLvl1";


    void Awake()
    {
        stars = GetComponentsInChildren<Image>();

        ms = Camera.main.GetComponent<MenuScript>();
        bttns = ms.levelChanger.GetComponentsInChildren<Button>();
    }


    void Start () {


        ///// Display Stars
        if (PlayerPrefs.GetInt(keyName) == 3)
        {
            int unlockLevel = levelChanger + 1;
            bttns[unlockLevel].interactable = true;

            stars[1].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
            stars[2].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
            stars[3].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
        }
        else if (PlayerPrefs.GetInt(keyName) == 2)
        {
            stars[1].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
            stars[2].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
        }
        else if (PlayerPrefs.GetInt(keyName) == 1)
        {
            stars[1].color = new Color(stars[0].color.r, stars[0].color.g, stars[0].color.b, 255);
        }


    }
	
}
