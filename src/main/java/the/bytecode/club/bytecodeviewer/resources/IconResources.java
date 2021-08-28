package the.bytecode.club.bytecodeviewer.resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Any resources loaded by disc or by memory.
 *
 * @author Konloch
 */

public class IconResources
{
    static protected final int HALF_SIZE = 4;
    static protected final int SIZE = 9;
    
    public static List<BufferedImage> iconList;
    public static BufferedImage icon;
    public static ImageIcon nextIcon;
    public static ImageIcon prevIcon;
    public static ImageIcon busyIcon;
    public static ImageIcon busyB64Icon;
    public static ImageIcon batIcon;
    public static ImageIcon shIcon;
    public static ImageIcon csharpIcon;
    public static ImageIcon cplusplusIcon;
    public static ImageIcon configIcon;
    public static ImageIcon jarIcon;
    public static ImageIcon zipIcon;
    public static ImageIcon packagesIcon;
    public static ImageIcon folderIcon;
    public static ImageIcon androidIcon;
    public static ImageIcon unknownFileIcon;
    public static ImageIcon textIcon;
    public static ImageIcon classIcon;
    public static ImageIcon imageIcon;
    public static ImageIcon decodedIcon;
    public static ImageIcon javaIcon;
    
    static
    {
        icon = b642IMG("iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAUd0lEQVR42pWaWXRbVZaGq5iHqgaSeJZsy7YkD7KtwZItebblQfI8x/HseIodO3bixE5iZw4ZSBwyACkCXQ003dD0oigq1UBqFVQ1HSB0wkyvXt1VPNSiavHCC288/b3/I11ZSszQDzuRfO89Z39n/3uffST9BMBP17Dbgna72B1hdmfQ7hK7W+yeoN0btPvE7v8Rdl+Y3Rsc4+7guHcF5wif9/ag3fYd/v70J/zHWlGFcLPRKqth99Yoc1TVKssTc1b74Krxw1Vbh3yxAl+9Mre/QZmnrvFHG+/Xnud4alwxzpEXnJOm+UGfbEH/wv2NAHkwMQ4P6GLk/1hlDyXFKVuXFI/1yQnKolJ0yqLTEhFjTEKsKRlxZgPi01OQkJ6qTJeRBn2mEYlZpjWN13gP7+VzfJ7G8WjRqXo1xwZDQmhe+kBfHhR7QHz7O300fq6LUhYBQkJ1UxDkFggZdEMQIJoTCkCsAhDn6TgdpKMWE5KyzcqSc9JDZsjNCL3WridZAmA3Q3F8zhMVBFpHELGHxJcHk2KVPZAYE4K5BYSkD+hjQuR8kAMQYENKgkwgUTBJFMzJgQhkpIrzRnHKJA6axdl0pFgzkGrNRJotS5nRbokw7e8pco8GRygugk4ixYXhAnGhOF90ml7Nvd5AX7SoRMKsGRElK7mJD9E4SFSqTg1KgLh0wy0AdF5z2uTIRrozV1lmvg2ZBQHLyLfK33KQnifX8nJgFuO9fC5VQaWr8RhRXWaaWijO92NgbAGQ2whyG5NIu0FJag0IDs5JOBkBtJXXnKfjWW47LG4HcgqdyC1yKePrDAFItaSjrrkZlf5aZBXYA4AuawgqHIgLxQXjvFTB98GEg9zOivCglhffAcHBExkFmSyVEZDJzQQQhyyePOSI07aSAjjKPMgrL4SroliZvbgAxpwsxCcnYmFxCecvXESO3J9bnK8gCa8BMaoE4kJpMFRBOMw6gXkoOT6Q0wSRIJCBIHcQRCW43EDqDWEQISkpGUkUZLJwADpkF+ed4nS+twTu6jJ4aspR5KtU5iwrRGqmGdHxsThw6GH8540PYfU4FSShrQIfDqRJjtHRpHYzDP3UYOh7BIjKizCImLBIECItGIV0mYzyCQeg83S6xF+FsvoaVDT6UNHkQ2WzH56qMqRlmRGTEIdXXn0Nn/3XfyOvxKPu98hzrspiNQ6BuDAZIlGTRIdRZ/T1QZjwnFkfBhMEuUOBcPNR0dCqk0psyYkwCA6uRYGTEqCgqlQ5pJwXx6ta61HT1ghfRzPqulrh72xBcXUFjJnikCEZX/71b3j5lcvweMvU/XyOz3MhOJ6t1I1siQ7nYdTDYeLCCgAXW4PhhqmB3EkQXogS2mgJoQbBnOBg5iAEJ+FkXEXKp7SuWjlU3dqgnG7obkdzTyda+zYq87U2wlnkRoopDTc++Bh/+cuXKCorRXldDfwCW9VSr57nOIW1FaHoMN/CYbiY9Id+xQRh1gfzJS8AcidB7mJLsCEsGvGSF1piU043Q2hR8LbUqdVv3NShHO8c6kX35gFsHO5H48Y2FFaUIiM7C+9eu64glvYdQk6eHcXectS3NaO5u0M9z0iWN9SqcZln4TBUAnOT/hAmVvKFix0VlFgECPsbai9cUoSgpJiAlJOCqAhAcFJGgfJp6e1SAD2jg+gbG1IgzRs7UFpVia6Nm1Qk/ud//4yz5x6HMcOM6lofnrz0Dzh3/hfo6utF86ZO1As0x2NucXwtMlw85gwXU5MYFzk8KvSdDAS5mw2bqlJCy8RiLWcZ5P7AxGZZVRASfkaiRiZtkMkZhY2b+9E/sRlDk2MKpLGjFUXlpZjfvgs3PvwEH3/yOfbvPwxjuhm/fOYf8e9vvysgzwhQLfwivc7BXrT1dytZMr+4SJrMuHicfy2JMSrMlXCQe9jFxgabP1Yplj5TUFLc1LgvsMIQolpkUC+RaBMIrv7g5CjGtk1hZOsWtG/qQrFAbN+xC1ffuaZs8/AI0rMy8MaVN/H21fewY24n7K481DT40SPPD2wZQffIINoHNikYRobzMAdZAMIlZpAughILj0oQ5G4FwjY60H6kqd4nPBr2Ug8KRLclPi+8Uk7rJKnDIcbntmJqfhaD4yPw+mrQ2NiE16/8Hr9784/o6elDVrZFVao3//Af6O7ugaekGM0dbRjdOqGem9g+jeGpcSVNRoZyZe6xlLMqUmL0g2U/PCparlBNZCDIfTwXaF0smzmjndGwSzTy4SwvEklVKv3WtjUpTXcN94mcRjA+uxXTu3Zgascs2ro7kV/oxpGDD+OV37yGixefRq7VionxSbz2xu/x9N8/B19DHQZGhrF99y4sHlzGrn17sG1xXsEMTY2pxWmVnGNF43zFzBeJSq4WFVGJIawcMyr54SA85Kg9wxLIDbP0RtluSfASt0SjFKX+alUqlaT6N6F3bBgj01uwded2zC/txuT2GdSKkzaHHXsXlvDiS7/C0p59sOU51PuXX/ktnnn2BYxOTuDQsaM4fuYUDj9yHEtHDwrMXswszKtFYa6xcDQyX0RiLMtuRiWYK1QJ/WMOa70Y1cRTJkHuJ4g+2Ayy32GlYtuQJ+1FoWi1vKEGvvYmVaG6JbmZ2JM7tmHH3gXsObQf2xd3oqG1GQ6XE16vV5L6n3Di2CNwFeSju6sbz7/wr3j+n1/C/gNH8MjZM3j0icdw8uyKgtl75IBajKn5OWyWPNsk+dLau1Gi0qKiwvmZo/SHjSkrqdaLMR0iQArrm0K9VGAHt6vdmzW92FelcoPRYEL2jQ9jdNukksTCgSUcOH4Eew/vx/D4KMq9FXA4nVjYuRtPXHwK3qpquPLzsXLqLC6JtC499QwOHDyIxy5dFJgLOPHoaRw88TB2H9yH2d07g1EZQYdUMs5HFZTI/JSXVZpP+mVy5Cj5Mw14fmFaUFUE+VkAJF2BsNRlMcklyZhsJRJeVhKGm2Fngm9hNJYW1WoePX0Cx8WhveJM56aNKJRkZiQO7T+Co4eOocDjRkVlJc6dewLnH38SS4t7ce7i4wrm1PlHceTUcSwzKsu7VfIPSeIzB5tkk2U5LpUKRj8oc/pF2ROERYkgVJMG8nOCJNsyVGebLocgljx2pu6aMpQ2VKO2owlNvZ1SJgcwPD2BrbvmsFO0ve/oIRw6eQwPnzqJA0cPY3JmGg3NTSguLYGnqBB75hcxsnkMnsJC7J5fwKmV85id3YaVC+fEzmLPgWVMz2/Hlu3bML1zToFsnqa8BpSMKWfKmvKiP9myMbN6pQWrF8twEOT+EIjBlgmjyCpDwpcjna2zskhqeYXqhfydzWiV0tgzOoSRmUlMyaTbJEFp01KxRqcmML5nAVv2L2Fibhua21pRXlmhgFrkdUlpKZb278P8rnlMTm9V0DM75tAiZXho2zTmDu7H7IF9GJb9aLOU5V6Rb5vIuK6rRXXQ3CBVnhQ51WnT6LCoPOHmHQFS1NCMFLu06XIczZBzQW6pdLfeYhT6pew2+VVDyIF7mB+zUypHugf7pBVpx+Dhneh/dDtGji6iV2S3eWwU/UMD8NXXobS8DCXSJBaJ3Ljj1/p96B4dwYgk9qaJUSVBp0jPXVGOscO7MHZ8D/okR/rGN0s+9oRAWP6dFUVKKQGQ1ZblVhChNLnkwORxKBBXVUkARAbyy4BtgwIyIWVXIHqkspRJL0X9dqxsRd2ZLvScmsPwyUUMHV/ExCMSmZNLGDy2gMkTSxgVB2ljx/Zg4uG9GDu0G91Sasu90sIXiWSsufANSJtydExanj6BEZDBntDmWOT3KoXkFAtIgYDkfS+InDmENrMwEqSSHW4YyGbJkY1DfSiuKBMHcpQTnqoK+Po60TEzis7FKWxankPv8nZ0755F5/wU2qZG0CiFoqqlUUXHH9wYB8dGUFvvh1U64s6js2jcJ/f2daNXgYi0NkaC5JbkC4hNpQDbFX12JIiqWioi+bkKxFrmhrN6NSI+GbBFVmzT+BCGZyYwtHUMrbKTl1fLzuspkI1PHNklSbo8g3x3AdyFHpXshcVFyviaVlpThVZpRYYlp3bI7j4kJbuithrt+6ZRd3pMnK5Hx0BgwbhwfpmX89MPSj1HgdgVSHIkyGr5NUhEjAKSoSIiIIxInRcVLX7UdjULiPRXY4MKZGJ+BpPz2zAoeq6u96kmsPPELPLP1sK70o+qlSHUr4yj9/wONJ+eRN3KKGrPDKPqXDfKzrZh+MRuDEk0muQQ1rl3Kxr2TaBICkt9e7N0DUNqwVpl4agEzu8REEdFoQJJl4ikUVpSZfU5kSBqQzTkWWAU/WUUOZBTVgCHt0g2G2nbm+UE2Cnlt1/OHSP9GJBojAvI3NKCql6N7a0qKlaHDcWSM22LW1C9bwydJ+fQviI92LFtqFwaQc3iKHxjvaiRHbu5pwteiYQqrdKMukuL1EGrR1qf/qlRdI32o0mkWiNlv1yqpluqFkGyJUfS3QEQgz0TOqlcESB8Y8iTiBTkIt1jR3ZpPmyVhXDWlMLtkzJaL7t7Wx3quqXXosSCkWGj1yqnvKKyEqXzmr52lLf4VM/FPkszQlrtNtidDlRUV6G5vQ1V0inz2Ov1VauKxkgMz2xB36Ts7Jt7UbepTfLTL3tZOezlHpF7AbKk/JoFJJURsUtEcs3azr7aayULSJpIyywgFgGxlrtV0rNZe/rZX+K996/h2vX38f6N67j+wQ1lNz78ANdv3MB7167htddfx9DFnYifM+PUSxfxzqfX8f5nHyp757PruPr5+3j783dx7fMPcOPjj/DBRx8qY9fM/z/65GM8/9KL2CiLxHz0yrnHKXtHdVMdrr73jti72LZnF8yy2KmiHoLoRFrBXmu1jU/Ky0SKKxsmt1SuYicsYmbpa5IzTHjrj3/At99++4PGHT7N6/pR92rmcLtw6syKev31119jZHZSJXmBHORMVgt+9eqv1bU//flPqv8zyhaRIiCJtnToJCLhIPfyTaIjEwanBWmUl+QJJWaQ/ishLQmv/+4KvvnmG7wh/8clJkBnTkFcmZzWii3QS7/Da7TlfcvYEB0Ver+0zPfRyqJiohEdGwN9UqKcGDORK3LLkvKdYjYiK9+BL//2V/XMv115XQ5VXlhcUgl7u0NjDU+Oq+6DqmEaJNrFt1xTxHnkngBIBpKdWQrEVGhTkUmSDjPOkIhf/+ZVfPXVV3jzrbfglx27fcsAyqe8qJvtQNNEj7pGm5EdOz4lMfR+z/ISdGkGJKYbZXXZWUt5L3HBXOVBqt+DzMZiGCWC8bKyW+dmQs8NSDXkZ8U3RL58z/nV5wguWeh8UYmoR28VEJFW8IQYOLPzjU5CRZBUudEoECzF/FIm1qCXg9K/4IsvvvhBe/vaVaTU2ULvdz55GMZdXmQv+8XqkLfcCveODngmO+EZaUGWvwyJIhWdOKgvtOClV15Wz1195yoW9uwOjZNfXoxUh0VFI8WZjSRRj17Kb7xEJPJTFHlDkPCopIjMdNJdRicn4JnnnsWnn36KK1euYEqavsmtk9gytWpHjh5R12l1XW2h1wvHDqGorxFlo51wDrXAvaUTjplOlC0OoGR5ALZjnXDtakdavQdRqUnSrhSGntVsVhpN7uKEoF/0Ty+JnmA1Iy7XGAGiPteKt5mgE90lOSXp87PVBhlvNiAqMR6/uPQkrkllevKpS4hN0iFaH4/ohFisj4nCA+seUs0hr9N8sqlpr2ePLiOztxbZIw2wjNYjc7wettk2uKc7YOmqgbGhHGZpy3UpyYhL0quxF/buDj1PSWW4pNy6AipJEbUwl3XBaMTmpEV8QKc+Mo2zEkQSOE+i4pJ+X17HyZl4Q2Iczsr54S3Jj8u/vYwLjz8WsvOPXcDZ8+fw1NNPqes0drva6xdefAHn5Pq58+eD/59bfX/hvBojU/Imxy0V0p4NvSkFaZIbly9fVs+zDVJduUBQ8owGVUP1xIu/casgqx9iM0zxNnMQJpBM/HJynS5WDkSn8brsEz9kzz33HAymNJxeWflR99PUuaeuElbZswwWM2KT9eiSanX60TOBz55FHZQUKyohwmUVm50a8SH2HXzDMDEqCazP6maT+gBsnT4WD8VHY11CDNbr4pTUopMSVBFgRYsXbSeI6YwpSDKnKtMbDdCn3Wq61OSQ8R5GwSXdg6fBC7u3ULXn8cZkxBh0MNosSt6MhEGKAfc5vSMSIsaSEvG1gvrGihcYKoaModPxgcwUxPAbVhk4OkWH2NRENVGCSRyTQpAkVS1ZSnRKdjpM/CyM3xvy2yd5bRJHzLbskJlsgb8ZZZMz5sp+YM1SZ3BHVTHyastgqypCZlGe6mrVV3z8ZoxVSiKSREkJCBc4zmoUkDRZeClEqyC3h0BiLKkBGEqMkREQwuhpUueTRGps1FSXLMmXLg0mD2FZMjmbOVuFR/QqTkm77RC55NHktbMqYHzNv7H5s8n5O1daIBtfC4BVopFdXiB7jFPywaYqJsssO41wCEqfqqF6YrIJkhrx1Zv6MpQgNEZFg2FkqEmGleGlVpl43DA5qaUsHznigLXSA5s4Y68WZ0UqTllhl68M+f7ykPE9/87rvM8uAHyGz3McjmcutMPksQXKv0CoUuvQImG6BSJKIhIEuS309TTDFAETJrNwGE6gdn+ZkBNnFOchq9QVgsqtcIfAFJw4rDlN4zXel122CsCWiIujVSctJ1hqVXLbAnlBnwK5ETD6HP6tbghEg9HyRYPhQIENMzMExAk1IDqhQdExDWwt4zXNeS0C4QCMgkps+2qZ1UrtzRBRWQYNZPW3KPxjOEwE0BpS44RahDQoJbswsLVM9XFB5/nMzQCBDS9dLZ4CCEaCdjME7ZYf1WzINIQufh/MzUA3Q4WDrWW8pjmvSehmGYWi8B1y0vxcEyTiJ05r/Mwp7wd+5vRdP2XiMTrc1vqZE8dZ62dOed/zMyfbWj9z+n/+8OyuNX54ds/3/OjsZzfZzT8+uzdsjO/68dkP/vDs/wBUXNeRym9KEQAAAABJRU5ErkJggg==");
        nextIcon = new ImageIcon(b642IMG("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAAMFBMVEX"
                + "///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv3aB7AAAABnRSTlMANzlYqPBJSG"
                + "/ZAAAASUlEQVR42mNgwAbS0oAEE4yHyWBmYAzjYDC694OJ4f9"
                + "+BoY3H0BSbz6A2MxA6VciFyDqGAWQTWVkYEkCUrcOsDD8OwtkvMViMwAb8xEUHlHcFAAAAABJRU5ErkJggg=="));
        prevIcon = new ImageIcon(b642IMG("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAAMFBMVEX"
                + "///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv3aB7AAAABnRSTlMANzlYgKhxpRi1AAAATElEQVR42mNgwAZYHIAEExA7qUAYLApMDmCGEwODCojByM/A8FEAyPi/moFh9QewYjCAM1iA+D2KqYwMrIlA6tUGFoa/Z4GMt1hsBgCe1wuKber+SwAAAABJRU5ErkJggg=="));
        busyIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/loading.gif")));
        busyB64Icon = new ImageIcon(b642IMG("R0lGODlhEAALAPQAAP"
                + "///wAAANra2tDQ0Orq6gcHBwAAAC8vL4KCgmFhYbq6uiMjI0tLS4qKimVlZb6+vicnJwUFBU9PT"
                + "+bm5tjY2PT09Dk5Odzc3PLy8ra2tqCgoMrKyu7u7gAAAAAAAAAAACH5BAkLAAAAIf4aQ3JlYXRlZCB3aXRoIGFqYXhsb2FkLmluZm8AIf8LTkVUU0NBUEUyLjADAQAAACwAAAAAEAALAAAFLSAgjmRpnqSgCuLKAq5AEIM4zDVw03ve27ifDgfkEYe04kDIDC5zrtYKRa2WQgAh+QQJCwAAACwAAAAAEAALAAAFJGBhGAVgnqhpHIeRvsDawqns0qeN5+y967tYLyicBYE7EYkYAgAh+QQJCwAAACwAAAAAEAALAAAFNiAgjothLOOIJAkiGgxjpGKiKMkbz7SN6zIawJcDwIK9W/HISxGBzdHTuBNOmcJVCyoUlk7CEAAh+QQJCwAAACwAAAAAEAALAAAFNSAgjqQIRRFUAo3jNGIkSdHqPI8Tz3V55zuaDacDyIQ+YrBH+hWPzJFzOQQaeavWi7oqnVIhACH5BAkLAAAALAAAAAAQAAsAAAUyICCOZGme1rJY5kRRk7hI0mJSVUXJtF3iOl7tltsBZsNfUegjAY3I5sgFY55KqdX1GgIAIfkECQsAAAAsAAAAABAACwAABTcgII5kaZ4kcV2EqLJipmnZhWGXaOOitm2aXQ4g7P2Ct2ER4AMul00kj5g0Al8tADY2y6C+4FIIACH5BAkLAAAALAAAAAAQAAsAAAUvICCOZGme5ERRk6iy7qpyHCVStA3gNa/7txxwlwv2isSacYUc+l4tADQGQ1mvpBAAIfkECQsAAAAsAAAAABAACwAABS8gII5kaZ7kRFGTqLLuqnIcJVK0DeA1r/u3HHCXC/aKxJpxhRz6Xi0ANAZDWa+kEAA7"));

        batIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/bat.png")));
        shIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/sh.png")));
        csharpIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/c#.png")));
        cplusplusIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/c++.png")));
        configIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/config.png")));
        jarIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/jar.png")));
        zipIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/zip.png")));
        packagesIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/package.png")));
        folderIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/folder.png")));
        androidIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/android.png")));
        unknownFileIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/file.png")));
        textIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/text.png")));
        classIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/class.png")));
        imageIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/image.png")));
        decodedIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/decoded.png")));
        javaIcon = new ImageIcon(Objects.requireNonNull(IconResources.class.getResource("/gui/java.png")));

        iconList = new ArrayList<>();
        int size = 16;
        for (int i = 0; i < 24; i++)
        {
            iconList.add(resize(icon, size, size));
            size += 2;
        }
    }
    
    public static String loadResourceAsString(String resourcePath) throws IOException
    {
        try (InputStream is = IconResources.class.getResourceAsStream(resourcePath)) {
            if (is == null)
                return null;
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, width, height);
    }

    /**
     * Decodes a Base64 String as a BufferedImage
     */
    public static BufferedImage b642IMG(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;

        try {
            imageByte = Base64.decodeBase64(imageString);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
                image = ImageIO.read(bis);
            }
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }

        return image;
    }
    
    
    /**
     * The minus sign button icon
     */
    public static class ExpandedIcon implements Icon, Serializable
    {
        static public Icon createExpandedIcon() {
            return new ExpandedIcon();
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Color backgroundColor = c.getBackground();
            if(backgroundColor != null)
                g.setColor(backgroundColor);
            else
                g.setColor(Color.white);
            
            g.fillRect(x, y, SIZE-1, SIZE-1);
            g.setColor(Color.gray);
            g.drawRect(x, y, SIZE-1, SIZE-1);
            g.setColor(Color.black);
            g.drawLine(x + 2, y + HALF_SIZE, x + (SIZE - 3), y + HALF_SIZE);
        }
        
        public int getIconWidth() {
            return SIZE;
        }
        
        public int getIconHeight() {
            return SIZE;
        }
    }
    
    /**
     * The plus sign button icon
     */
    public static class CollapsedIcon extends ExpandedIcon
    {
        static public Icon createCollapsedIcon() {
            return new CollapsedIcon();
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            super.paintIcon(c, g, x, y);
            g.drawLine(x + HALF_SIZE, y + 2, x + HALF_SIZE, y + (SIZE - 3));
        }
    }
}
