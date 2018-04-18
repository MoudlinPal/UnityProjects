using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MenuController : MonoBehaviour
{
    private const string FACEBOOK_URL = "https://www.facebook.com/N3ken/";

    public void OnPlayClick()
    {
        UnityEngine.SceneManagement.SceneManager.LoadScene("Game");
    }

    public void OnRateClick()
    {
        Application.OpenURL(FACEBOOK_URL);
    }

    public void SoundClick()
    {

    }

    public void TutorialClick()
    {

    }
}