using System.Collections;
using System.Collections.Generic;
using UnityEngine;
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
   
   

    public GameObject panPrefab; // Our Panels

    public ScrollRect scrollRect; // FOR inertia

    private GameObject[] instPans; // сюда сохраняем spawned objects
    private Vector2[] pansPos; // позиции панелей
    private Vector2[] pansScale; 

    private RectTransform contentRect; // RectTransform of CONTENT
    private Vector2 contentVector;

    private int selectedPanID; 

    private bool isScrolling; 


    private void Start()
    {
        contentRect = GetComponent<RectTransform>(); 

        instPans = new GameObject[panCount];
        pansPos = new Vector2[panCount];
        pansScale = new Vector2[panCount];

        for(int i = 0; i < panCount; i++)
        {                // Instantiate - create object | panPrefab - what we creat, transform - where we creat it
            instPans[i] = Instantiate(panPrefab, transform, false); // скрипт на контенте => трансформ и есть контент | false значит, что мы юзаем только локальные координаты

            if (i == 0) continue; // позиция 0 панельки не учитывается

                     // устанваливаем позицию кажой панели                                                     
            instPans[i].transform.localPosition = new Vector2(instPans[i-1].transform.localPosition.x + panPrefab.GetComponent<RectTransform>().sizeDelta.x + panOffset,
                instPans[i].transform.localPosition.y);

            // закидываем в массив позиций панелей
            pansPos[i] = -instPans[i].transform.localPosition;
        }
        
       
    }







    private void FixedUpdate() // обновляется в зависимости от фпса 
    {
        // to due to inertia мы не улетали далеко при сильном скроллинге
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
            float distance = Mathf.Abs(contentRect.anchoredPosition.x - pansPos[i].x); // abs - абсолютн значение

            if(distance < nearestPos)
            {
                nearestPos = distance;
                selectedPanID = i;
            }
            ////// !! nearest Panel to CONTENT (to ur screen) !! //////////////////////////////////////////////////////////






            /////////////////////////////////////// !! SCALE !! //////////////////////////////////////////////////////////
            // 1 - полный размер обьекта
            float scale = Mathf.Clamp(1 / (distance / panOffset) * scaleOffset, 0.5f, 1f);
            pansScale[i].x = Mathf.SmoothStep(instPans[i].transform.localScale.x, scale + 0.3f, scaleSpeed * Time.fixedDeltaTime);
            pansScale[i].y = Mathf.SmoothStep(instPans[i].transform.localScale.y, scale + 0.3f, scaleSpeed * Time.fixedDeltaTime);

            instPans[i].transform.localScale = pansScale[i];
            /////////////////////////////////////// !! SCALE !! //////////////////////////////////////////////////////////



        }


        float scrollVelocity = Mathf.Abs(scrollRect.velocity.x);
        //Debug.Log(scrollVelocity); // checking
        // TURN OFF inertia (без дерганой аним. и с инерцией)
        if (scrollVelocity < 400 && !isScrolling) scrollRect.inertia = false;



        // if true - не идем дальше
        if (isScrolling || scrollVelocity > 400) return; // if we scroll => не привязываем screen to Panel


        // отпустили скроллинг и наш контент подьехал к ближайшей пнельки
        contentVector.x = Mathf.SmoothStep(contentRect.anchoredPosition.x, pansPos[selectedPanID].x, snapSpeed * Time.fixedDeltaTime);
        contentRect.anchoredPosition = contentVector;

    }


    




    public void Scrolling(bool scroll)
    {
        isScrolling = scroll;

        // TURN ON inertia if we're scrolling
        if (scroll) scrollRect.inertia = true;
    }

}
