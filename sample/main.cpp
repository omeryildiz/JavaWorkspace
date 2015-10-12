#include <iostream>
#include <string.h>

using namespace std;

int pointValue(struct Point *);
void pointString(const string &);

struct Point {
    char data[20];
    int value;
};

typedef struct Point Point;

int main()
{
    Point *poPointer;
    Point po;

    string myString;
    poPointer = new Point;
    strcpy(poPointer->data,"omer yildiz");
    poPointer->value = 98;
    char buffer[sizeof(poPointer)];
    memcpy(buffer,poPointer,sizeof(poPointer));
    pointString(buffer);
    strcpy(po.data,"dennis");
    po.value = 4;
    struct Point *point = new Point;
    strcpy(point->data,"merhaba dunya");
    point->value = 20;
    cout << "point data = " << point->data <<endl;
    cout << "point value = " << point->value <<endl;

    pointValue(point);
    cout << "point data = " << point->data <<endl;
    cout << "point value = " << point->value <<endl;

    delete[] poPointer;
    return 0;
}

int pointValue(struct Point *p) {
    int ok = 10;
    strcpy(p->data,"degisen dunya");
    p->value = 30;
    return ok;
}

void pointString(const string &testString)
{
    Point *poPointer;
    poPointer = new Point;
    char buff[sizeof(testString)];
    strncpy(buff, testString.c_str(), sizeof(buff));
    memcpy(poPointer,&buff,sizeof(buff));
    cout <<poPointer->data<<endl;
    cout << poPointer->value<<endl;
    cout << "Test string calisiyor" << endl;
    cout << testString << endl;
    cout << "pointer verimiz = " << poPointer->data <<  endl;
    cout << "pointer degerimiz = " << poPointer->value << endl;

    delete[] poPointer;
}

