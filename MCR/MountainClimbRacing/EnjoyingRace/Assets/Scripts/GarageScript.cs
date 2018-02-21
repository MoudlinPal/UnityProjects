using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class GarageScript : MonoBehaviour
{
    [Range(1, 50)]
    [Header("Controllers")]
    public int panCount;
    [Range(0, 500)]
    public int panOffset;
    [Range(0f, 20f)]
    public float snapSpeed;
    [Range(0f, 10f)]
    public float scaleOffset;
    [Range(1f, 20f)]
    public float scaleSpeed;
    [Header("Other Objects")]
   
   

    public GameObject panPrefab;             // Our Panels
    //private GameObject improvementInterface; // Child object of panelPrefab


    public ScrollRect scrollRect; // FOR inertia

    private GameObject[] spawnedPanels; // сюда сохраняем spawned objects
    private GameObject[] spawnedImpInterface; // сюда сохраняем spawned child objects
    private Vector2[] pansPos; // позиции панелей
    private Vector2[] pansScale; 

    private RectTransform contentRect; // RectTransform of CONTENT
    private Vector2 contentVector;

    private int selectedPanID; 

    private bool isScrolling; 


    private void Start()
    {
        contentRect = GetComponent<RectTransform>(); 

        spawnedPanels = new GameObject[panCount];
        pansPos = new Vector2[panCount];
        pansScale = new Vector2[panCount];

        for(int i = 0; i < panCount; i++)
        {

            // created and added Panel to array  // Instantiate - create object | panPrefab - what we creat, transform - where we creat it
            spawnedPanels[i] = Instantiate(panPrefab, transform, false); // скрипт на контенте => трансформ и есть контент | false значит, что мы юзаем только локальные координаты


            // get Interface of our Panel
            //spawnedImpInterface[i] = spawnedPanels[i].gameObject.GetComponent<GameObject>();
            //spawnedImpInterface[i].SetActive(false);


            if (i == 0) continue; // позиция 0 панельки не учитывается

                     // устанваливаем позицию кажой панели                                                     
            spawnedPanels[i].transform.localPosition = new Vector2(spawnedPanels[i-1].transform.localPosition.x + panPrefab.GetComponent<RectTransform>().sizeDelta.x + panOffset,
                spawnedPanels[i].transform.localPosition.y);

            // закидываем в массив позиций панелей
            pansPos[i] = -spawnedPanels[i].transform.localPosition;
        }
        
       
    }







    private void FixedUpdate() // обновляется в зависимости от фпса 
    {
        // due to inertia мы не улетали далеко при сильном скроллинге
        if(contentRect.anchoredPosition.x >= pansPos[0].x && !isScrolling || contentRect.anchoredPosition.x <= pansPos[pansPos.Length-1].x && !isScrolling)
        {
            scrollRect.inertia = false; // если не скролится, то выключаем инерцию
        }


        // nearest to Panel
        float nearestPos = float.MaxValue; // в начале макс позиция, чтобы при переборе в цикле он уменьшался и шел к меньшему


        for (int i = 0; i < panCount; i++)
        {

            ////// !! nearest Panel to CONTENT (to ur screen) !! //////////////////////////////////////////////////////////
            // nearest to Panel
            float distance = Mathf.Abs(contentRect.anchoredPosition.x - pansPos[i].x); // abs - абсолютн значение (молуль)

            if(distance < nearestPos)
            {
                nearestPos = distance;
                selectedPanID = i;
            }
            ////// !! nearest Panel to CONTENT (to ur screen) !! //////////////////////////////////////////////////////////
            



            /////////////////////////////////////// !! SCALE !! //////////////////////////////////////////////////////////
            // 1 - полный размер обьекта
            float scale = Mathf.Clamp(1 / (distance / panOffset) * scaleOffset, 0.5f, 1f);
            pansScale[i].x = Mathf.SmoothStep(spawnedPanels[i].transform.localScale.x, scale + 0.3f, scaleSpeed * Time.fixedDeltaTime);
            pansScale[i].y = Mathf.SmoothStep(spawnedPanels[i].transform.localScale.y, scale + 0.3f, scaleSpeed * Time.fixedDeltaTime);

            spawnedPanels[i].transform.localScale = pansScale[i];
            /////////////////////////////////////// !! SCALE !! //////////////////////////////////////////////////////////




            // If KeyCode *BACK* was pressed
            if (Input.GetKeyDown(KeyCode.Escape))
            {
                SceneManager.LoadScene(0);
            }

        }


        float scrollVelocity = Mathf.Abs(scrollRect.velocity.x);
        // Debug.Log(scrollVelocity); // checking
        // TURN OFF inertia (без дерганой аним. и с инерцией)
        if (scrollVelocity < 400 && !isScrolling) scrollRect.inertia = false;



        // if true - не идем дальше
        if (isScrolling || scrollVelocity > 400) return; // if we scroll => не привязываем screen to Panel


        // отпустили скроллинг и наш контент подьехал к ближайшей пaнельки
        contentVector.x = Mathf.SmoothStep(contentRect.anchoredPosition.x, pansPos[selectedPanID].x, snapSpeed * Time.fixedDeltaTime);
        contentRect.anchoredPosition = contentVector;



        // If there's no inertion - show Improvement Interface of selected Panel
        /*if (scrollRect.inertia == false)
        {
            spawnedImpInterface[selectedPanID].SetActive(true);
        }
        else
        {
            spawnedImpInterface[selectedPanID].SetActive(false);
        }*/
        

    }


    




    public void Scrolling(bool scroll)
    {
        isScrolling = scroll;

        // TURN ON inertia if we're scrolling
        if (scroll) scrollRect.inertia = true;
    }


    


}
